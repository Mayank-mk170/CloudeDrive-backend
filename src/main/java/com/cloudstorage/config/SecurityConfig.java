package com.cloudstorage.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final GoogleOAuth2 googleOAuth2;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(
            JWTFilter jwtFilter,
            GoogleOAuth2 googleOAuth2,
            RateLimitFilter rateLimitFilter
    ) {
        this.jwtFilter = jwtFilter;
        this.googleOAuth2 = googleOAuth2;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf -> csrf.disable())


                // ==========================================
                // CORS
                // ==========================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth

                        // ----------------------------------
                        // CORS PRE-FLIGHT
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // ----------------------------------
                        // SWAGGER / OPENAPI
                        // ----------------------------------

                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()


                        // ----------------------------------
                        // NORMAL AUTHENTICATION
                        // ----------------------------------

                        .requestMatchers(
                                "/api/v1/users/api/auth/register",
                                "/api/v1/users/api/auth/login"
                        ).permitAll()


                        // ----------------------------------
                        // GOOGLE OAUTH2
                        // ----------------------------------

                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()


                        // ----------------------------------
                        // PUBLIC LINKS
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/public-links/**"
                        ).permitAll()


                        // ----------------------------------
                        // ADMIN
                        // ----------------------------------

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")


                        // ----------------------------------
                        // EVERYTHING ELSE
                        // ----------------------------------

                        .anyRequest().authenticated()
                )


                // ==========================================
                // EXCEPTION HANDLING
                // ==========================================

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"error\":\"Unauthorized\"}"
                                    );
                                }
                        )
                )


                // ==========================================
                // GOOGLE OAUTH2 LOGIN
                // ==========================================

                .oauth2Login(oauth2 ->
                        oauth2.successHandler(
                                googleOAuth2
                        )
                )


                // ==========================================
                // RATE LIMIT FILTER
                // ==========================================

                .addFilterBefore(
                        rateLimitFilter,
                        UsernamePasswordAuthenticationFilter.class
                )


                // ==========================================
                // JWT FILTER
                // ==========================================

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // ==========================================
    // CORS CONFIGURATION
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                List.of(
                        "http://localhost:*",
                        "https://*.railway.app",
                        "https://cloudstorage-frontend24.vercel.app",
                        "https://cloudedrive-backend-production.up.railway.app"


                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}