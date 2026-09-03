package com.cloudstorage.controller;

import com.cloudstorage.dto.CreateFolderRequest;
import com.cloudstorage.dto.RenameFolderRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }


    // ==========================================
    // CREATE
    // POST /api/folders
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
    // ROOT FOLDERS
    // GET /api/folders
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getRootFolders() {

        User user = getCurrentUser();

        return folderService.getRootFolders(
                user.getEmail()
        );
    }


    // ==========================================
    // RECYCLE BIN FOLDERS
    // GET /api/folders/trash
    // ==========================================

    @GetMapping("/trash")
    public ResponseEntity<?> getTrashFolders() {

        User user = getCurrentUser();

        return folderService.getTrashFolders(
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
    // CHILD FOLDERS
    // GET /api/folders/{id}/children
    // ==========================================

    @GetMapping("/{id}/children")
    public ResponseEntity<?> getChildFolders(
            @PathVariable Long id
    ) {

        User user = getCurrentUser();

        return folderService.getChildFolders(
                id,
                user.getEmail()
        );
    }


    // ==========================================
    // RENAME
    // PUT /api/folders/{id}
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> renameFolder(
            @PathVariable Long id,
            @Valid @RequestBody RenameFolderRequest request
    ) {

        User user = getCurrentUser();

        return folderService.renameFolder(
                id,
                request.name(),
                user.getEmail()
        );
    }


    // ==========================================
    // MOVE TO RECYCLE BIN
    // DELETE /api/folders/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolder(
            @PathVariable Long id
    ) {

        User user = getCurrentUser();

        return folderService.deleteFolder(
                id,
                user.getEmail()
        );
    }


    // ==========================================
    // RESTORE
    // PUT /api/folders/{id}/restore
    // ==========================================

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreFolder(
            @PathVariable Long id
    ) {

        User user = getCurrentUser();

        return folderService.restoreFolder(
                id,
                user.getEmail()
        );
    }


    // ==========================================
    // PERMANENT DELETE
    // DELETE /api/folders/{id}/permanent
    // ==========================================

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteFolder(
            @PathVariable Long id
    ) {

        User user = getCurrentUser();

        return folderService.permanentlyDeleteFolder(
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