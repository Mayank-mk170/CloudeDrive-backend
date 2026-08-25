package com.cloudstorage.controller;


import com.cloudstorage.dto.CreateFolderRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // CREATE FOLDER
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createFolder(
            @Valid @RequestBody CreateFolderRequest request
    ) {

        User user = getCurrentUser();

        return folderService.createFolder(
                request,
                user.getEmail()
        );
    }


    // ==========================================
    // GET FOLDER
    // GET /api/folders/{id}
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFolder(
            @PathVariable Long id
    ) {

        User user = getCurrentUser();

        return folderService.getFolder(
                id,
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
