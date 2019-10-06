package com.thoughtmechanix.zuulserver.util.context;

import com.thoughtmechanix.zuulserver.model.UserContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/*
* To inject headers into outgoing RestTemplate requests
* */
public class UserContextInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add(UserContext.CORRELATION_ID,
                UserContextHolder
                        .getContext()
                        .getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN,
                UserContextHolder
                        .getContext()
                        .getAuthToken());
        return execution.execute(request, body);
    }
}
