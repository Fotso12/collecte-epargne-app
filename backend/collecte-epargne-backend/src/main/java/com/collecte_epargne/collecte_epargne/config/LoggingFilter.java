package com.collecte_epargne.collecte_epargne.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Capture IP Address (Remote Address)
            String remoteAddr = httpRequest.getHeader("X-Forwarded-For");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = httpRequest.getRemoteAddr();
            }
            MDC.put("remoteAddress", remoteAddr);

            // Capture User ID / Login from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                MDC.put("userId", authentication.getName());
            } else {
                MDC.put("userId", "anonymous");
            }

            chain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}
