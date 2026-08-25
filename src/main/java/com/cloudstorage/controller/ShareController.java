package com.cloudstorage.controller;

import com.cloudstorage.dto.ShareRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.service.ShareService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    private final ShareService shareService;

    public ShareController(
            ShareService shareService
    ) {
        this.shareService = shareService;
    }

    // ==========================================
    // CREATE SHARE
    // POST /api/shares
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createShare(
            @Valid @RequestBody ShareRequest request
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return shareService.createShare(
                request,
                user.getEmail()
        );
    }
}