package com.cloudstorage.service;

import com.cloudstorage.dto.ShareRequest;
import com.cloudstorage.model.File;
import com.cloudstorage.model.Share;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.ShareRepository;
import com.cloudstorage.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ShareService {

    private final ShareRepository shareRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public ShareService(
            ShareRepository shareRepository,
            FileRepository fileRepository,
            UserRepository userRepository
    ) {
        this.shareRepository = shareRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE SHARE
    // ==========================================

    public ResponseEntity<?> createShare(
            ShareRequest request,
            String ownerEmail
    ) {

        // Find owner
        Optional<User> optionalOwner =
                userRepository.findByEmail(ownerEmail);

        if (optionalOwner.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User owner =
                optionalOwner.get();


        // Find file
        Optional<File> optionalFile =
                fileRepository.findByIdAndUserEmail(
                        request.fileId(),
                        ownerEmail
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();


        // Don't share files in Trash
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }


        // Find user receiving the share
        Optional<User> optionalSharedUser =
                userRepository.findById(
                        request.sharedWithUserId()
                );

        if (optionalSharedUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Shared user not found");
        }

        User sharedWithUser =
                optionalSharedUser.get();


        // Don't share with yourself
        if (owner.getId()
                .equals(sharedWithUser.getId())) {

            return ResponseEntity
                    .badRequest()
                    .body("You cannot share a file with yourself");
        }


        // Check existing share
        boolean alreadyShared =
                shareRepository
                        .existsByFileIdAndSharedWithUserId(
                                file.getId(),
                                sharedWithUser.getId()
                        );

        if (alreadyShared) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("File is already shared with this user");
        }


        // Validate expiry
        if (request.expiresAt() != null
                && request.expiresAt()
                .isBefore(OffsetDateTime.now())) {

            return ResponseEntity
                    .badRequest()
                    .body("Expiry time must be in the future");
        }


        // Create share
        Share share =
                new Share();

        share.setFile(file);

        share.setSharedWithUser(
                sharedWithUser
        );

        share.setPermission(
                request.permission()
        );

        share.setExpiresAt(
                request.expiresAt()
        );

        share.setCreatedAt(
                OffsetDateTime.now()
        );


        Share savedShare =
                shareRepository.save(share);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedShare.getId());
    }
}