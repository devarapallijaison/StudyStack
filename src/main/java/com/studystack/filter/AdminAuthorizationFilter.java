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

@WebFilter("/admin/*")
public class AdminAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if ("/admin/login.jsp".equals(path) || "/admin/auth".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        var session = httpRequest.getSession(false);
        Object adminId = session == null ? null : session.getAttribute("ADMIN_ID");
        Object role = session == null ? null : session.getAttribute("ROLE");
        if (adminId == null || !"ADMIN".equals(role)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login.jsp");
            return;
        }
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(request, response);
    }
}
