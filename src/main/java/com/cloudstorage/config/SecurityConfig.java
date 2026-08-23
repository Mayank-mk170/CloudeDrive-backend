package com.cloudstorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;


@Configuration
public class SecurityConfig {

    private JWTFilter jwtFilter;

    public SecurityConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

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
}