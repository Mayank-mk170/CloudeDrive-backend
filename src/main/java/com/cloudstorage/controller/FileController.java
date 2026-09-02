package com.cloudstorage.controller;

import com.cloudstorage.dto.CompleteUploadRequest;
import com.cloudstorage.dto.InitUploadRequest;
import com.cloudstorage.dto.RenameFileRequest;
import com.cloudstorage.model.User;
import com.cloudstorage.service.FileService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // ==========================================
    // INIT UPLOAD
    // ==========================================

    @PostMapping("/init-upload")
    public ResponseEntity<?> initUpload(
            @Valid @RequestBody InitUploadRequest request
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = (User) principal;

        return fileService.initUpload(
                request,
                user.getEmail()
        );
    }


    // ==========================================
    // COMPLETE UPLOAD
    // ==========================================

    @PostMapping("/complete-upload")
    public ResponseEntity<?> completeUpload(
            @Valid @RequestBody CompleteUploadRequest request
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = (User) principal;

        return fileService.completeUpload(
                request,
                user.getEmail()
        );
    }


    // ==========================================
// GET MY FILES
// GET /api/files
// ==========================================

    @GetMapping
    public ResponseEntity<?> getMyFiles() {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.getMyFiles(
                user.getEmail()
        );
    }
    // ==========================================
// GET FILE
// ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(
            @PathVariable Long id
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = (User) principal;

        return fileService.getFile(
                id,
                user.getEmail()
        );
    }

    // ==========================================
// GET FILE PREVIEW URL
// GET /api/files/{id}/preview
// ==========================================

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> previewFile(
            @PathVariable Long id
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.generatePreviewUrl(
                id,
                user.getEmail()
        );
    }

// ==========================================
// DELETE FILE
// ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long id
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = (User) principal;

        return fileService.deleteFile(
                id,
                user.getEmail()
        );
    }

    // ==========================================
// RENAME FILE
// PUT /api/files/{id}
// ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> renameFile(
            @PathVariable Long id,
            @Valid @RequestBody RenameFileRequest request
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.renameFile(
                id,
                request,
                user.getEmail()
        );
    }

    // ==========================================
// SEARCH FILES WITH PAGINATION
// ==========================================

    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.searchFiles(
                name,
                user.getEmail(),
                page,
                size
        );
    }

    // ==========================================
// GET TRASH FILES
// GET /api/files/trash
// ==========================================

    @GetMapping("/trash")
    public ResponseEntity<?> getTrashFiles() {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.getTrashFiles(
                user.getEmail()
        );
    }


// ==========================================
// RESTORE FILE
// PUT /api/files/{id}/restore
// ==========================================

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreFile(
            @PathVariable Long id
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.restoreFile(
                id,
                user.getEmail()
        );
    }

    // ==========================================
// PERMANENT DELETE FILE
// DELETE /api/files/{id}/permanent
// ==========================================

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteFile(
            @PathVariable Long id
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user =
                (User) principal;

        return fileService.permanentlyDeleteFile(
                id,
                user.getEmail()
        );
    }
}