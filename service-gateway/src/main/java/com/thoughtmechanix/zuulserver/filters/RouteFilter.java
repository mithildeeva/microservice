package com.thoughtmechanix.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.thoughtmechanix.zuulserver.model.ABTestingRoute;
import com.thoughtmechanix.zuulserver.util.FilterUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class RouteFilter extends ZuulFilter {
    @Autowired
    FilterUtils filterUtils;
    @Autowired
    RestTemplate restTemplate;

    private static final int FILTER_ORDER =  1;
    private static final boolean SHOULD_FILTER =true;
    private static final String TEST_ROUTE_SERVICE = "http://specialroutesservice/v1/route/abtesting/{serviceName}";

    @Override
    public String filterType() {
        return FilterUtils.ROUTE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();

        ABTestingRoute testRoute = getTestingRoute(filterUtils.getServiceId());

        if (testRoute == null || !shouldUseTestRoute(testRoute)) return null;

        String route = buildRoute(ctx.getRequest().getRequestURI(),
                testRoute.getEndpoint(),
                ctx.get("serviceId").toString());
        forwardToTestRoute(route);

        return null;
    }

    private ABTestingRoute getTestingRoute(String serviceName) {
        // not required (to AB Test)
        return null;
//        ResponseEntity<ABTestingRoute> restExchange;
//        try {
//            restExchange = restTemplate
//                    .exchange(TEST_ROUTE_SERVICE, HttpMethod.GET, null, ABTestingRoute.class, serviceName);
//        } catch (HttpClientErrorException ex) {
//            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) return null;
//            throw ex;
//        }
//        return restExchange.getBody();
    }

    private boolean shouldUseTestRoute(ABTestingRoute route) {
        if (route.getActive().equals("N")) return false;

        int randdomNumber = (new Random()).nextInt(10) + 1;

        return route.getWeight() < randdomNumber;
    }

    private String buildRoute(String oldEndpoint, String newEndpoint, String serviceName){
        int index = oldEndpoint.indexOf(serviceName);

        String strippedRoute = oldEndpoint.substring(index + serviceName.length());
        System.out.println("Target route: " + String.format("%s/%s", newEndpoint, strippedRoute));
        return String.format("%s/%s", newEndpoint, strippedRoute);
    }

    private ProxyRequestHelper helper
            = new ProxyRequestHelper ();
    private void forwardToTestRoute(String route) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        MultiValueMap<String, String> headers = helper.buildZuulRequestHeaders(request);
        MultiValueMap<String, String> params = helper.buildZuulRequestQueryParams(request);

        InputStream requestEntity = getRequestBody(request);

        if (request.getContentLength() < 0) ctx.setChunkedRequestBody();

        helper.addIgnoredHeaders();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = forward(httpClient, getVerb(request), route, request, headers, params, requestEntity);
            setResponse(response);
        } catch (Exception ex ) {
            ex.printStackTrace();

        }
    }

    private InputStream getRequestBody(HttpServletRequest request) {
        InputStream requestEntity = null;
        try {
            requestEntity = request.getInputStream();
        }
        catch (IOException ex) {
            // no requestBody is ok.
        }
        return requestEntity;
    }

    private String getVerb(HttpServletRequest request) {
        return request.getMethod().toUpperCase();
    }

    private HttpResponse forward(HttpClient httpclient, String verb, String uri,
                                 HttpServletRequest request, MultiValueMap<String, String> headers,
                                 MultiValueMap<String, String> params, InputStream requestEntity)
            throws Exception {
        URL host = new URL( uri );
        HttpHost httpHost = getHttpHost(host);

        HttpRequest httpRequest;
        int contentLength = request.getContentLength();
        InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength,
                request.getContentType() != null
                        ? ContentType.create(request.getContentType()) : null);
        switch (verb.toUpperCase()) {
            case "POST":
                HttpPost httpPost = new HttpPost(uri);
                httpRequest = httpPost;
                httpPost.setEntity(entity);
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(uri);
                httpRequest = httpPut;
                httpPut.setEntity(entity);
                break;
            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(uri );
                httpRequest = httpPatch;
                httpPatch.setEntity(entity);
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uri);

        }
        try {
            httpRequest.setHeaders(convertHeaders(headers));
            return httpclient.execute(httpHost, httpRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private HttpHost getHttpHost(URL host) {
        HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(),
                host.getProtocol());
        return httpHost;
    }

    private Header[] convertHeaders(MultiValueMap<String, String> headers) {
        List<Header> list = new ArrayList<>();
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                list.add(new BasicHeader(name, value));
            }
        }
        return list.toArray(new BasicHeader[0]);
    }

    private void setResponse(HttpResponse response) throws IOException {
        this.helper.setResponse(response.getStatusLine().getStatusCode(),
                response.getEntity() == null ? null : response.getEntity().getContent(),
                revertHeaders(response.getAllHeaders()));
    }

    private MultiValueMap<String, String> revertHeaders(Header[] headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (Header header : headers) {
            String name = header.getName();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<String>());
            }
            map.get(name).add(header.getValue());
        }
        return map;
    }
}
