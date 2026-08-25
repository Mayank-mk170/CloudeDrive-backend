package com.cloudstorage.service;

import com.cloudstorage.dto.CreateFolderRequest;
import com.cloudstorage.dto.FolderResponse;
import com.cloudstorage.model.Folder;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private UserRepository userRepository;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    // CREATE FOLDER
    // ==========================================

    public ResponseEntity<?> createFolder(
            CreateFolderRequest request,
            String userEmail
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User user =
                optionalUser.get();

        Folder parentFolder = null;

        if (request.parentId() != null) {

            Optional<Folder> optionalParent =
                    folderRepository.findByIdAndUserEmail(
                            request.parentId(),
                            userEmail
                    );

            if (optionalParent.isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Parent folder not found");
            }

            parentFolder =
                    optionalParent.get();
        }

        // Create folder without Lombok builder
        Folder folder = new Folder();

        folder.setName(request.name());
        folder.setUser(user);
        folder.setParent(parentFolder);

        Folder savedFolder =
                folderRepository.save(folder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedFolder));
    }


    // ==========================================
    // GET FOLDER
    // ==========================================

    public ResponseEntity<?> getFolder(
            Long folderId,
            String userEmail
    ) {

        Optional<Folder> optionalFolder =
                folderRepository
                        .findByIdAndUserEmail(
                                folderId,
                                userEmail
                        );

        if (optionalFolder.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Folder not found");
        }

        Folder folder =
                optionalFolder.get();

        return ResponseEntity.ok(
                toResponse(folder)
        );
    }


    // ==========================================
    // GET ROOT FOLDERS
    // ==========================================

    public ResponseEntity<?> getRootFolders(
            String userEmail
    ) {

        List<Folder> folders =
                folderRepository
                        .findByUserEmailAndParentIsNullOrderByNameAsc(
                                userEmail
                        );

        List<FolderResponse> response =
                folders.stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // GET CHILD FOLDERS
    // ==========================================

    public ResponseEntity<?> getChildFolders(
            Long parentId,
            String userEmail
    ) {

        // First verify that parent belongs
        // to the logged-in user
        Optional<Folder> parent =
                folderRepository
                        .findByIdAndUserEmail(
                                parentId,
                                userEmail
                        );

        if (parent.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Parent folder not found");
        }

        List<Folder> folders =
                folderRepository
                        .findByUserEmailAndParentIdOrderByNameAsc(
                                userEmail,
                                parentId
                        );

        List<FolderResponse> response =
                folders.stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private FolderResponse toResponse(
            Folder folder
    ) {

        Long parentId = null;

        if (folder.getParent() != null) {

            parentId =
                    folder.getParent().getId();
        }

        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                parentId,
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }
}
