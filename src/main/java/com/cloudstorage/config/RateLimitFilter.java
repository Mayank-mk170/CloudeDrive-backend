package com.cloudstorage.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets =
            new ConcurrentHashMap<>();

    private Bucket createBucket() {

        Bandwidth limit =
                Bandwidth.builder()
                        .capacity(100)
                        .refillGreedy(
                                100,
                                Duration.ofMinutes(1)
                        )
                        .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket getBucket(String ip) {

        return buckets.computeIfAbsent(
                ip,
                key -> createBucket()
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ip =
                request.getRemoteAddr();

        Bucket bucket =
                getBucket(ip);

        if (bucket.tryConsume(1)) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // ==========================================
        // RATE LIMIT EXCEEDED
        // ==========================================

        response.setStatus(429);

        response.setContentType("application/json");

        response.getWriter().write(
                "{\"error\":\"Too many requests. Please try again later.\"}"
        );

        response.setContentType(
                "application/json"
        );

        response.getWriter().write(
                "{\"error\":\"Too many requests. Please try again later.\"}"
        );
    }
}