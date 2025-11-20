package com.app.demo.Config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // Log para debug
        System.out.println("🚫 AccessDeniedException capturada!");
        System.out.println("🚫 Request URI: " + request.getRequestURI());
        System.out.println("🚫 Request Method: " + request.getMethod());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("🚫 Authentication: " + auth.getName());
            System.out.println("🚫 Authorities: " + auth.getAuthorities());
            System.out.println("🚫 IsAuthenticated: " + auth.isAuthenticated());
        } else {
            System.out.println("🚫 Authentication es NULL en AccessDeniedHandler");
        }
        
        System.out.println("🚫 Exception: " + accessDeniedException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String errorMessage = "Access Denied";
        if (auth != null) {
            errorMessage = "Access Denied. User: " + auth.getName() + ", Authorities: " + auth.getAuthorities();
        } else {
            errorMessage = "Access Denied. Authentication is null";
        }
        
        response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"" + errorMessage + "\"}");
    }
}

