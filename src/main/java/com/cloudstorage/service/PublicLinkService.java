package com.cloudstorage.service;

import com.cloudstorage.dto.PublicLinkRequest;
import com.cloudstorage.model.File;
import com.cloudstorage.model.PublicLink;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.PublicLinkRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicLinkService {

    private final PublicLinkRepository publicLinkRepository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;

    public PublicLinkService(
            PublicLinkRepository publicLinkRepository,
            FileRepository fileRepository, S3Service s3Service
    ) {
        this.publicLinkRepository = publicLinkRepository;
        this.fileRepository = fileRepository;
        this.s3Service = s3Service;
    }

    // ==========================================
    // CREATE PUBLIC LINK
    // ==========================================

    public ResponseEntity<?> createPublicLink(
            PublicLinkRequest request,
            String userEmail
    ) {

        // Find file owned by logged-in user
        Optional<File> optionalFile =
                fileRepository.findByIdAndUserEmail(
                        request.fileId(),
                        userEmail
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();

        // Don't create link for Trash file
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        // Check whether file already has a public link
        if (publicLinkRepository.existsByFileId(file.getId())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Public link already exists for this file");
        }

        // Validate expiry
        if (request.expiresAt() != null
                && request.expiresAt()
                .isBefore(OffsetDateTime.now())) {

            return ResponseEntity
                    .badRequest()
                    .body("Expiry time must be in the future");
        }

        // Generate secure random token
        String token =
                UUID.randomUUID().toString();

        // Create public link
        PublicLink publicLink =
                new PublicLink();

        publicLink.setToken(token);

        publicLink.setFile(file);

        publicLink.setCreatedAt(
                OffsetDateTime.now()
        );

        publicLink.setExpiresAt(
                request.expiresAt()
        );

        PublicLink savedLink =
                publicLinkRepository.save(
                        publicLink
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedLink.getToken());
    }

    // ==========================================
// GET PUBLIC FILE
// ==========================================

    public ResponseEntity<?> getPublicFile(
            String token
    ) {

        Optional<PublicLink> optionalLink =
                publicLinkRepository.findByToken(token);

        if (optionalLink.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Public link not found");
        }

        PublicLink publicLink =
                optionalLink.get();

        // Check expiry
        if (publicLink.getExpiresAt() != null
                && publicLink.getExpiresAt()
                .isBefore(OffsetDateTime.now())) {

            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body("Public link has expired");
        }

        File file =
                publicLink.getFile();

        // Don't allow access to deleted files
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        // Generate signed download URL
        String downloadUrl =
                s3Service.generateDownloadUrl(
                        file.getS3Key()
                );

        return ResponseEntity.ok(downloadUrl);
    }
}