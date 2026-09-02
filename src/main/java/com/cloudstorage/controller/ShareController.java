package com.cloudstorage.controller;

import com.cloudstorage.model.User;
import com.cloudstorage.service.ShareService;

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
            @RequestBody
            com.cloudstorage.dto.ShareRequest request
    ) {

        User user =
                getCurrentUser();

        return shareService.createShare(
                request,
                user.getEmail()
        );
    }


    // ==========================================
    // SHARED BY ME
    // GET /api/shares/shared-by-me
    // ==========================================

    @GetMapping("/shared-by-me")
    public ResponseEntity<?> getSharesCreatedByMe() {

        User user =
                getCurrentUser();

        return shareService.getSharesCreatedByMe(
                user.getEmail()
        );
    }


    // ==========================================
    // SHARED WITH ME
    // GET /api/shares/shared-with-me
    // ==========================================

    @GetMapping("/shared-with-me")
    public ResponseEntity<?> getFilesSharedWithMe() {

        User user =
                getCurrentUser();

        return shareService.getFilesSharedWithMe(
                user.getEmail()
        );
    }


    // ==========================================
    // CURRENT USER
    // ==========================================

    private User getCurrentUser() {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return (User) principal;
    }
}