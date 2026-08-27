package com.cloudstorage.config;

import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private UserRepository userRepository;

    public JWTFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authorizationHeader =
//                request.getHeader("Authorization");
//
//        if (authorizationHeader != null
//                && authorizationHeader.startsWith("Bearer ")) {
//
//            String token =
//                    authorizationHeader.substring(7);
//
//            try {
//
//                // Get email from JWT
//                String email =
//                        jwtService.getEmail(token);
//
//                // Find user using email
//                Optional<User> optionalUser =
//                        userRepository.findByEmail(email);
//
//                if (optionalUser.isPresent()
//                        && SecurityContextHolder
//                        .getContext()
//                        .getAuthentication() == null) {
//
//                    User user = optionalUser.get();
//
//                    // User is the principal
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    user,
//                                    null,
//                                    Collections.emptyList()
//                            );
//
//                    authentication.setDetails(
//                            new WebAuthenticationDetailsSource()
//                                    .buildDetails(request)
//                    );
//
//                    SecurityContextHolder
//                            .getContext()
//                            .setAuthentication(authentication);
//                }
//
//            } catch (Exception e) {
//
//                response.setStatus(
//                        HttpServletResponse.SC_UNAUTHORIZED
//                );
//
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);



//@Override
//protected void doFilterInternal(
//        HttpServletRequest request,
//        HttpServletResponse response,
//        FilterChain filterChain
//) throws ServletException, IOException {
//
//    String authorization =
//            request.getHeader("Authorization");
//
//    if (authorization != null
//            && authorization.startsWith("Bearer ")) {
//
//        String token =
//                authorization.substring(7);
//
//        try {
//
//            String email =
//                    jwtService.getEmail(token);
//
//            System.out.println("JWT EMAIL = " + email);
//
//            Optional<User> optionalUser =
//                    userRepository.findByEmail(email);
//
//            System.out.println(
//                    "USER FOUND = " + optionalUser.isPresent()
//            );
//
//            if (optionalUser.isPresent()) {
//
//                User user = optionalUser.get();
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                user,
//                                null,
//                                Collections.emptyList()
//                        );
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource()
//                                .buildDetails(request)
//                );
//
//                SecurityContextHolder
//                        .getContext()
//                        .setAuthentication(authentication);
//
//                System.out.println(
//                        "AUTHENTICATION SET = "
//                                + SecurityContextHolder
//                                .getContext()
//                                .getAuthentication()
//                );
//            }
//
//        } catch (Exception e) {
//
//            System.out.println(
//                    "JWT ERROR = " + e.getMessage()
//            );
//
//            response.setStatus(
//                    HttpServletResponse.SC_UNAUTHORIZED
//            );
//
//            return;
//        }
//    }
//
//    filterChain.doFilter(request, response);
//    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization =
                request.getHeader("Authorization");

        // ==========================================
        // NO JWT
        // ==========================================

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            // Important:
            // Do NOT return 401 here.
            //
            // OAuth2 requests don't have a JWT yet.
            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // GET JWT
        // ==========================================

        String token =
                authorization.substring(7);

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
            // FIND USER
            // ==========================================

            Optional<User> optionalUser =
                    userRepository.findByEmail(email);

            System.out.println(
                    "USER FOUND = "
                            + optionalUser.isPresent()
            );

            // ==========================================
            // SET AUTHENTICATION
            // ONLY IF NOT ALREADY AUTHENTICATED
            // ==========================================

            if (optionalUser.isPresent()
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                User user =
                        optionalUser.get();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

                System.out.println(
                        "AUTHENTICATION SET = "
                                + SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR = "
                            + e.getMessage()
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
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
