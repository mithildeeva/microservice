package com.thoughtmechanix.organizations.util.context;

import com.thoughtmechanix.organizations.model.UserContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext()
                .setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
        UserContextHolder.getContext()
                .setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext()
                .setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext()
                .setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

        System.out.println("UserContextFilter Correlation id: " + UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}
