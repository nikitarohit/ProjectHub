package com.projectmanagement.clientprojectmanagement.filter;

import com.projectmanagement.clientprojectmanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path   = request.getRequestURI();
        String method = request.getMethod();

        // Allow OPTIONS (CORS preflight)
        if ("OPTIONS".equals(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        // Allow static files — no token needed
        if (path.endsWith(".html") || path.endsWith(".js") ||
                path.endsWith(".css")  || path.endsWith(".ico") ||
                path.endsWith(".png")  || path.endsWith(".svg") ||
                path.endsWith(".woff2")|| path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow login and register — no token needed
        if (path.equals("/users/login") || path.equals("/users/register")) {
            chain.doFilter(request, response);
            return;
        }

        // ── ALL other requests (GET, POST, PUT, PATCH, DELETE) need JWT ──
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"No token provided.\"}");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token.\"}");
            return;
        }

        // ── Token valid — set authentication in SecurityContext ──
        try {
            String email = jwtUtil.extractEmail(token);
            String role  = jwtUtil.extractRole(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + (role != null ? role.toUpperCase() : "USER")))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token processing failed.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}