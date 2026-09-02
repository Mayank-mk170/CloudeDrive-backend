package com.cloudstorage.service;

import com.cloudstorage.dto.ShareRequest;
import com.cloudstorage.dto.ShareResponse;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Optional<User> optionalOwner =
                userRepository.findByEmail(ownerEmail);

        if (optionalOwner.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User owner =
                optionalOwner.get();

        // ==========================================
        // FIND FILE
        // ==========================================

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

        // ==========================================
        // DON'T SHARE TRASH FILE
        // ==========================================

        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        // ==========================================
        // FIND RECEIVER
        // ==========================================

        Optional<User> optionalSharedUser =
                userRepository.findByEmail(
                        request.email()
                );

        if (optionalSharedUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            "User with this email does not exist"
                    );
        }

        User sharedWithUser =
                optionalSharedUser.get();

        // ==========================================
        // DON'T SHARE WITH YOURSELF
        // ==========================================

        if (owner.getId()
                .equals(sharedWithUser.getId())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "You cannot share a file with yourself"
                    );
        }

        // ==========================================
        // CHECK EXISTING SHARE
        // ==========================================

        boolean alreadyShared =
                shareRepository
                        .existsByFileIdAndSharedWithUserId(
                                file.getId(),
                                sharedWithUser.getId()
                        );

        if (alreadyShared) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "File is already shared with this user"
                    );
        }

        // ==========================================
        // VALIDATE EXPIRY
        // ==========================================

        if (
                request.expiresAt() != null
                        &&
                        request.expiresAt()
                                .isBefore(OffsetDateTime.now())
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Expiry time must be in the future"
                    );
        }

        // ==========================================
        // CREATE SHARE
        // ==========================================

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

        // ==========================================
        // SAVE
        // ==========================================

        Share savedShare =
                shareRepository.save(share);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedShare.getId());
    }


    // ==========================================
    // GET SHARES CREATED BY ME
    // ==========================================

    public ResponseEntity<?> getSharesCreatedByMe(
            String ownerEmail
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(ownerEmail);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User user =
                optionalUser.get();

        List<Share> shares =
                shareRepository
                        .findByFileUserIdAndFileDeletedFalse(
                                user.getId()
                        );

        List<ShareResponse> response =
                shares.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // GET FILES SHARED WITH ME
    // ==========================================

    public ResponseEntity<?> getFilesSharedWithMe(
            String email
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User user =
                optionalUser.get();

        List<Share> shares =
                shareRepository
                        .findBySharedWithUserIdAndFileDeletedFalse(
                                user.getId()
                        );

        List<ShareResponse> response =
                shares.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // CONVERT SHARE → RESPONSE
    // ==========================================

    private ShareResponse toResponse(
            Share share
    ) {

        File file =
                share.getFile();

        User sharedWithUser =
                share.getSharedWithUser();

        return new ShareResponse(

                share.getId(),

                file.getId(),

                file.getOriginalFileName(),

                file.getContentType(),

                file.getSize(),

                file.getS3Key(),

                sharedWithUser.getId(),

                sharedWithUser.getEmail(),

                share.getPermission(),

                share.getExpiresAt(),

                share.getCreatedAt()
        );
    }
}