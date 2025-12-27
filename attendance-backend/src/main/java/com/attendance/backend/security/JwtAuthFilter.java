package com.attendance.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // =====================================================
    // 🚫 DO NOT APPLY JWT FILTER ON THESE PATHS
    // =====================================================
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.equals("/auth/student/login")
                || path.equals("/api/teacher/login")
                || path.startsWith("/admin")
                || path.startsWith("/administration")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images");
    }

    // =====================================================
    // JWT AUTHENTICATION
    // =====================================================
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 🔒 Do NOT override existing authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = extractToken(request);

            if (token != null && !token.isBlank() && jwtUtil.isTokenValid(token)) {

                Long userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);
                // ADMIN | ADMINISTRATION | TEACHER | STUDENT

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    // =====================================================
    // TOKEN EXTRACTION
    // =====================================================
    private String extractToken(HttpServletRequest request) {

        // 1️⃣ Authorization header (Mobile / API)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }

        // 2️⃣ Cookie (Admin / Administration Web)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    return cookie.getValue().trim();
                }
            }
        }

        return null;
    }
}
