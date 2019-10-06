package com.thoughtmechanix.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.thoughtmechanix.zuulserver.util.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponsePostFilter extends ZuulFilter {
    @Autowired
    FilterUtils filterUtils;

    private static final int FILTER_ORDER=1;
    private static final boolean SHOULD_FILTER=true;

    @Override
    public String filterType() {
        return FilterUtils.POST_FILTER_TYPE;
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
        System.out.println("Adding Correlation ID to response headers {}." + filterUtils.getCorrelationId());

        ctx.getResponse()
                .addHeader(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId());

        System.out.println("Completing request for  {}." + ctx.getRequest().getRequestURI());
        return null;
    }
}
