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

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ==========================================
        // GOOGLE OAUTH2
        // DO NOT PROCESS GOOGLE REQUESTS WITH JWT
        // ==========================================

        String path = request.getServletPath();

        if (path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")) {

            System.out.println(
                    "JWT FILTER SKIPPED FOR OAUTH2 = " + path
            );

            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // GET AUTHORIZATION HEADER
        // ==========================================

        String authorization =
                request.getHeader("Authorization");

        // ==========================================
        // NO TOKEN
        // ==========================================

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorization.substring(7).trim();

        // ==========================================
        // CHECK EMPTY TOKEN
        // ==========================================

        if (token.isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            // ==========================================
            // GET EMAIL FROM JWT
            // ==========================================

            String email =
                    jwtService.getEmail(token);

            System.out.println(
                    "JWT EMAIL = " + email
            );

            // ==========================================
            // CHECK EMAIL
            // ==========================================

            if (email == null || email.isBlank()) {

                System.out.println(
                        "JWT ERROR = Email claim is empty"
                );

                filterChain.doFilter(request, response);
                return;
            }

            // ==========================================
            // FIND USER
            // ==========================================

            Optional<User> optionalUser =
                    userRepository.findByEmail(email);

            if (optionalUser.isEmpty()) {

                System.out.println(
                        "JWT ERROR = User not found: "
                                + email
                );

                filterChain.doFilter(request, response);
                return;
            }

            User user =
                    optionalUser.get();

            System.out.println(
                    "USER FOUND = " + user.getEmail()
            );

            // ==========================================
            // ROLE
            // ==========================================

            String role =
                    user.getRole() != null
                            ? user.getRole().name()
                            : "USER";

            System.out.println(
                    "USER ROLE = " + role
            );

            // ==========================================
            // AUTHORITY
            // ==========================================

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            // ==========================================
            // AUTHENTICATION
            // ==========================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // ==========================================
            // SECURITY CONTEXT
            // ==========================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "AUTHENTICATION SUCCESS = "
                            + user.getEmail()
            );

        } catch (Exception e) {

            // ==========================================
            // JWT ERROR
            // ==========================================

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "JWT VALIDATION FAILED"
            );

            System.out.println(
                    "ERROR TYPE = "
                            + e.getClass().getName()
            );

            System.out.println(
                    "ERROR MESSAGE = "
                            + e.getMessage()
            );

            e.printStackTrace();

            System.out.println(
                    "================================="
            );

            SecurityContextHolder
                    .clearContext();

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // ==========================================
        // CONTINUE
        // ==========================================

        filterChain.doFilter(
                request,
                response
        );
    }
}