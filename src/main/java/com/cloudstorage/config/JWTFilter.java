package com.cloudstorage.config;

import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTFilter(
            JWTService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    // =====================================================
    // SKIP JWT FILTER FOR PUBLIC ENDPOINTS
    // =====================================================

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path = request.getServletPath();

        // Swagger / OpenAPI
        if (path.startsWith("/v3/api-docs")) {
            return true;
        }

        if (path.startsWith("/swagger-ui")) {
            return true;
        }

        if (path.startsWith("/swagger-resources")) {
            return true;
        }

        if (path.startsWith("/webjars")) {
            return true;
        }

        if (path.equals("/swagger-ui.html")) {
            return true;
        }

        // Register / Login
        if (path.startsWith(
                "/api/v1/users/api/auth"
        )) {
            return true;
        }

        // Google OAuth2
        if (path.startsWith("/oauth2")) {
            return true;
        }

        if (path.startsWith("/login/oauth2")) {
            return true;
        }

        // Public links
        if (path.startsWith("/api/public-links")) {
            return true;
        }

        return false;
    }


    // =====================================================
    // JWT FILTER
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization =
                request.getHeader("Authorization");


        // =================================================
        // NO AUTHORIZATION HEADER
        // =================================================

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // =================================================
        // GET TOKEN
        // =================================================

        String token =
                authorization.substring(7).trim();


        // Empty token
        if (token.isEmpty()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        try {

            // =================================================
            // GET EMAIL FROM JWT
            // =================================================

            String email =
                    jwtService.getEmail(token);

            System.out.println(
                    "JWT EMAIL = " + email
            );


            // =================================================
            // CHECK EMAIL
            // =================================================

            if (email == null
                    || email.trim().isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }


            // =================================================
            // FIND USER
            // =================================================

            Optional<User> optionalUser =
                    userRepository.findByEmail(email);


            if (optionalUser.isEmpty()) {

                System.out.println(
                        "USER NOT FOUND = " + email
                );

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }


            // =================================================
            // GET USER
            // =================================================

            User user =
                    optionalUser.get();


            // =================================================
            // GET ROLE
            // =================================================

            String role =
                    user.getRole().name();

            System.out.println(
                    "USER ROLE = " + role
            );


            // =================================================
            // CREATE AUTHORITY
            // =================================================

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );


            // =================================================
            // CREATE AUTHENTICATION
            // =================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(
                                    authority
                            )
                    );


            // =================================================
            // ADD REQUEST DETAILS
            // =================================================

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );


            // =================================================
            // SET SECURITY CONTEXT
            // =================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );


            System.out.println(
                    "AUTHENTICATION SET = "
                            + authentication
            );

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR = "
                            + e.getMessage()
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid or expired token\"}"
            );

            return;
        }


        // =================================================
        // CONTINUE REQUEST
        // =================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}