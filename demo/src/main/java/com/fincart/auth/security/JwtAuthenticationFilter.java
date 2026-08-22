package com.fincart.auth.security;

import com.fincart.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "AUTH HEADER = " + authHeader
        );

        // 2. No JWT
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "NO JWT FOUND"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove "Bearer "
        String token =
                authHeader.substring(7);

        // 4. Validate JWT
        boolean valid =
                jwtService.isTokenValid(token);

        System.out.println(
                "JWT VALID = " + valid
        );

        // 5. Invalid/expired JWT
        if (!valid) {

            System.out.println(
                    "JWT INVALID OR EXPIRED"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // 6. Extract email from JWT
        String email =
                jwtService.extractEmail(token);

        System.out.println(
                "JWT EMAIL = " + email
        );

        // 7. Create Authentication object
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        // 8. Put authentication into SecurityContext
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // 9. Debug authentication
        System.out.println(
                "AUTHENTICATION SET = "
                        + SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        System.out.println(
                "AUTHENTICATED = "
                        + SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .isAuthenticated()
        );

        System.out.println(
                "AUTH USER = "
                        + SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        System.out.println(
                "AUTHORITIES = "
                        + SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
        );

        // 10. Continue request
        filterChain.doFilter(request, response);
    }
}