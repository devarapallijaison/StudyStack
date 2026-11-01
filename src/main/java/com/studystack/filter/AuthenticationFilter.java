package com.studystack.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/student/*")
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Object userId = httpRequest.getSession(false) == null ? null
                : httpRequest.getSession(false).getAttribute("USER_ID");
        Object role = httpRequest.getSession(false) == null ? null
                : httpRequest.getSession(false).getAttribute("ROLE");
        if (userId == null || !"STUDENT".equals(role)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login.jsp");
            return;
        }
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(request, response);
    }
}
