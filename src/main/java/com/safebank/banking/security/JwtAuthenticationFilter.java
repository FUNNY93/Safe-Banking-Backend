package com.safebank.banking.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // CORS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // PUBLIC CREATE ACCOUNT
        if ("POST".equalsIgnoreCase(method)
                && "/users".equals(path)) {
            return true;
        }

        // ADMIN LOGIN
        if ("/admin/login".equals(path)) {
            return true;
        }

        // OTP/Auth
        if (path.startsWith("/auth/")) {
            return true;
        }

        // Google OAuth
        if (path.startsWith("/oauth2/")) {
            return true;
        }

        if (path.startsWith("/login/")) {
            return true;
        }

        return false;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println(
            "JWT FILTER: "
            + request.getMethod()
            + " "
            + request.getRequestURI()
        );

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println(
            "AUTH HEADER: "
            + authorizationHeader
        );

        // No Authorization header
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        try {

            String email =
                    jwtService.extractEmail(token);

            System.out.println(
                "JWT EMAIL: " + email
            );

            if (email != null
                    && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

                if (jwtService.isTokenValid(
                        token,
                        email)) {

                    String role =
                            jwtService.extractRole(token);
                    if (role != null) {
                        role = role.toUpperCase();
                    }

                    System.out.println("JWT ROLE: " + role);

                    String authority = "ROLE_" + role;

                    System.out.println("AUTHORITY: " + authority);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    java.util.Collections.singletonList(
                                            new SimpleGrantedAuthority(authority)
                                    )
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "AUTHENTICATION: " +
                            SecurityContextHolder.getContext().getAuthentication()
                    );
                } else {
                    System.out.println("JWT ROLE is missing");
                }
            }

        } catch (Exception e) {

            System.out.println(
                "JWT validation failed: "
                + e.getMessage()
            );
        }

        filterChain.doFilter(
            request,
            response
        );
    }
}