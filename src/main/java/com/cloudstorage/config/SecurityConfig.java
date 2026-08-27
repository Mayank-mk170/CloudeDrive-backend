package com.cloudstorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    private JWTFilter jwtFilter;
    private final GoogleOAuth2 googleOAuth2;

    public SecurityConfig(JWTFilter jwtFilter, GoogleOAuth2 googleOAuth2) {
        this.jwtFilter = jwtFilter;
        this.googleOAuth2 = googleOAuth2;
    }

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        // Disable CSRF
        http.csrf(csrf -> csrf.disable());

        // Disable CORS
        http.cors(cors -> cors.disable());

        // Add JWT filter
        http.addFilterBefore(
                jwtFilter,
                AuthorizationFilter.class
        );

        // Authorization rules
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/v1/users/api/auth/register",
                        "/api/v1/users/api/auth/login"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
        );

        return http.build();
    }

     */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/users/api/auth/register",
                                "/api/v1/users/api/auth/login"
                        ).permitAll()

                        // Google OAuth2
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/public-links/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                // ==========================================
                // GOOGLE OAUTH2 LOGIN
                // ==========================================

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(
                                googleOAuth2
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                List.of("http://localhost:*")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
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