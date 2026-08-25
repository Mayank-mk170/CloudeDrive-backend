package com.cloudstorage.service;

import com.cloudstorage.dto.CompleteUploadRequest;
import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.dto.InitUploadRequest;
import com.cloudstorage.dto.InitUploadResponse;
import com.cloudstorage.dto.RenameFileRequest;
import com.cloudstorage.model.File;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public FileService(
            FileRepository fileRepository,
            UserRepository userRepository,
            S3Service s3Service
    ) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }


    // ==========================================
    // INIT UPLOAD
    // ==========================================

    public ResponseEntity<?> initUpload(
            InitUploadRequest request,
            String email
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user =
                optionalUser.get();


        // Generate unique file name
        String storedFileName =
                UUID.randomUUID()
                        + "-"
                        + request.fileName();


        // Create S3 key
        String s3Key =
                "users/"
                        + user.getId()
                        + "/"
                        + storedFileName;


        // Generate signed upload URL
        String uploadUrl =
                s3Service.generateUploadUrl(
                        s3Key,
                        request.contentType()
                );


        // Create File entity
        File file =
                new File();

        file.setOriginalFileName(
                request.fileName()
        );

        file.setStoredFileName(
                storedFileName
        );

        file.setS3Key(
                s3Key
        );

        file.setContentType(
                request.contentType()
        );

        file.setSize(
                request.size()
        );

        file.setCreatedAt(
                OffsetDateTime.now()
        );

        file.setUser(
                user
        );

        // New file is not deleted
        file.setDeleted(false);


        // Save metadata
        File savedFile =
                fileRepository.save(file);


        // Create response
        InitUploadResponse response =
                new InitUploadResponse(
                        savedFile.getId(),
                        uploadUrl,
                        s3Key
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // COMPLETE UPLOAD
    // ==========================================

    public ResponseEntity<?> completeUpload(
            CompleteUploadRequest request,
            String email
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user =
                optionalUser.get();


        Optional<File> optionalFile =
                fileRepository.findById(
                        request.fileId()
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();


        // Check ownership
        if (!file.getUser()
                .getId()
                .equals(user.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "You do not have permission to access this file"
                    );
        }


        // Don't complete a deleted file
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }


        return ResponseEntity.ok(
                toResponse(file)
        );
    }


    // ==========================================
    // GET FILE
    // ==========================================

    public ResponseEntity<?> getFile(
            Long fileId,
            String userEmail
    ) {

        Optional<File> optionalFile =
                fileRepository.findByIdAndUserEmail(
                        fileId,
                        userEmail
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();


        // Don't return files in Trash
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }


        return ResponseEntity.ok(
                toResponse(file)
        );
    }


    // ==========================================
    // DELETE FILE → TRASH
    // ==========================================

    public ResponseEntity<?> deleteFile(
            Long fileId,
            String userEmail
    ) {

        Optional<File> optionalFile =
                fileRepository.findByIdAndUserEmail(
                        fileId,
                        userEmail
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();


        // Already in trash
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }


        /*
         * Soft delete.
         *
         * We DO NOT delete the S3 object.
         * We only mark the database record as deleted.
         */
        file.setDeleted(true);

        fileRepository.save(file);

        return ResponseEntity.ok(
                "File moved to trash"
        );
    }


    // ==========================================
    // RENAME FILE
    // ==========================================

    public ResponseEntity<?> renameFile(
            Long fileId,
            RenameFileRequest request,
            String userEmail
    ) {

        Optional<File> optionalFile =
                fileRepository.findByIdAndUserEmail(
                        fileId,
                        userEmail
                );

        if (optionalFile.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        File file =
                optionalFile.get();


        // Don't rename files in Trash
        if (file.isDeleted()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }


        file.setOriginalFileName(
                request.fileName()
        );


        File updatedFile =
                fileRepository.save(file);


        return ResponseEntity.ok(
                toResponse(updatedFile)
        );
    }


    // ==========================================
    // FILE ENTITY → RESPONSE DTO
    // ==========================================

    private FileResponse toResponse(
            File file
    ) {

        return new FileResponse(
                file.getId(),
                file.getOriginalFileName(),
                file.getStoredFileName(),
                file.getContentType(),
                file.getSize(),
                file.getS3Key(),
                file.getCreatedAt()
        );
    }
}