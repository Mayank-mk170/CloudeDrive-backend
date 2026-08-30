package com.cloudstorage.controller;

import com.cloudstorage.model.User;
import com.cloudstorage.service.StarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stars")
public class StarController {

    private final StarService starService;

    public StarController(StarService starService) {
        this.starService = starService;
    }

    // ==========================================
    // STAR
    // POST /api/stars/{fileId}
    // ==========================================

    @PostMapping("/{fileId}")
    public ResponseEntity<?> starFile(
            @PathVariable Long fileId
    ) {

        User user = getCurrentUser();

        return starService.starFile(
                fileId,
                user.getEmail()
        );
    }

    // ==========================================
    // UNSTAR
    // DELETE /api/stars/{fileId}
    // ==========================================

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> unstarFile(
            @PathVariable Long fileId
    ) {

        User user = getCurrentUser();

        return starService.unstarFile(
                fileId,
                user.getEmail()
        );
    }

    // ==========================================
    // GET STARRED
    // GET /api/stars
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getStarredFiles() {

        User user = getCurrentUser();

        return starService.getStarredFiles(
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