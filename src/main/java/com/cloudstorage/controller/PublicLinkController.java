package com.cloudstorage.controller;

import com.cloudstorage.dto.PublicLinkRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.service.PublicLinkService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public-links")
public class PublicLinkController {

    private final PublicLinkService publicLinkService;

    public PublicLinkController(
            PublicLinkService publicLinkService
    ) {
        this.publicLinkService = publicLinkService;
    }

    // ==========================================
    // CREATE PUBLIC LINK
    // POST /api/public-links
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createPublicLink(
            @Valid @RequestBody PublicLinkRequest request
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return publicLinkService.createPublicLink(
                request,
                user.getEmail()
        );
    }

    //GET PUBLIC FILE
    // GET /api/public-links/{token}
    // ==========================================

    @GetMapping("/{token}")
    public ResponseEntity<?> getPublicFile(
            @PathVariable String token
    ) {

        return publicLinkService.getPublicFile(
                token
        );
    }
}