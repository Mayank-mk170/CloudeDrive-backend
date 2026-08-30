package com.cloudstorage.service;

import com.cloudstorage.model.File;
import com.cloudstorage.model.Star;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.StarRepository;
import com.cloudstorage.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StarService {

    private final StarRepository starRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public StarService(
            StarRepository starRepository,
            FileRepository fileRepository,
            UserRepository userRepository
    ) {
        this.starRepository = starRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // STAR FILE
    // ==========================================

    public ResponseEntity<?> starFile(
            Long fileId,
            String email
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        File file =
                fileRepository.findByIdAndUserEmail(
                        fileId,
                        email
                ).orElse(null);

        if (file == null || file.isDeleted()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        if (starRepository.existsByUserIdAndFileId(
                user.getId(),
                fileId
        )) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("File is already starred");
        }

        Star star = new Star();

        star.setUser(user);
        star.setFile(file);
        star.setCreatedAt(OffsetDateTime.now());

        Star saved =
                starRepository.save(star);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved.getId());
    }

    // ==========================================
    // UNSTAR FILE
    // ==========================================

    public ResponseEntity<?> unstarFile(
            Long fileId,
            String email
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Star star =
                starRepository
                        .findByUserIdAndFileId(
                                user.getId(),
                                fileId
                        )
                        .orElse(null);

        if (star == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File is not starred");
        }

        starRepository.delete(star);

        return ResponseEntity.ok(
                "File unstarred successfully"
        );
    }

    // ==========================================
    // GET STARRED FILES
    // ==========================================

    public ResponseEntity<?> getStarredFiles(
            String email
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        List<Star> stars =
                starRepository
                        .findByUserIdAndFileDeletedFalse(
                                user.getId()
                        );

        return ResponseEntity.ok(stars);
    }
}