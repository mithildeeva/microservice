package com.thoughtmechanix.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.thoughtmechanix.zuulserver.util.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TrackingPreFilter extends ZuulFilter {
    @Autowired
    FilterUtils filterUtils;

    private static final int      FILTER_ORDER =  1;
    private static final boolean  SHOULD_FILTER=true;

    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
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
        System.out.println("Processing incoming request for {}. " + ctx.getRequest().getRequestURI());
        if (isCorrelationIdPresent()) System.out.println("tmx-correlation-id found in tracking filter: {}. " + filterUtils.getCorrelationId());
        else {
            filterUtils.setCorrelationId(generateCorrelationId());
            System.out.println("tmx-correlation-id generated in tracking filter: {}. " + filterUtils.getCorrelationId());
        }
        return null;
    }

    private boolean isCorrelationIdPresent() {
        return filterUtils.getCorrelationId() != null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
