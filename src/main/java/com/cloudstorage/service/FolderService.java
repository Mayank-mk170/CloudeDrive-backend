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

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderService(
            FolderRepository folderRepository,
            UserRepository userRepository
    ) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
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
                    folderRepository
                            .findByIdAndUserEmailAndDeletedFalse(
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

        Folder folder = new Folder();

        folder.setName(request.name().trim());
        folder.setUser(user);
        folder.setParent(parentFolder);
        folder.setDeleted(false);

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
                        .findByIdAndUserEmailAndDeletedFalse(
                                folderId,
                                userEmail
                        );

        if (optionalFolder.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Folder not found");
        }

        return ResponseEntity.ok(
                toResponse(optionalFolder.get())
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
                        .findByUserEmailAndParentIsNullAndDeletedFalseOrderByNameAsc(
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

        Optional<Folder> parent =
                folderRepository
                        .findByIdAndUserEmailAndDeletedFalse(
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
                        .findByUserEmailAndParentIdAndDeletedFalseOrderByNameAsc(
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
    // RENAME FOLDER
    // ==========================================

    public ResponseEntity<?> renameFolder(
            Long folderId,
            String newName,
            String userEmail
    ) {

        Optional<Folder> optionalFolder =
                folderRepository
                        .findByIdAndUserEmailAndDeletedFalse(
                                folderId,
                                userEmail
                        );

        if (optionalFolder.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Folder not found");
        }

        String name =
                newName == null
                        ? ""
                        : newName.trim();

        if (name.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Folder name cannot be empty");
        }

        Folder folder =
                optionalFolder.get();

        folder.setName(name);

        Folder savedFolder =
                folderRepository.save(folder);

        return ResponseEntity.ok(
                toResponse(savedFolder)
        );
    }


    // ==========================================
    // MOVE FOLDER TO RECYCLE BIN
    // ==========================================

    public ResponseEntity<?> deleteFolder(
            Long folderId,
            String userEmail
    ) {

        Optional<Folder> optionalFolder =
                folderRepository
                        .findByIdAndUserEmailAndDeletedFalse(
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

        /*
         * We keep the existing rule:
         * don't delete a folder that contains
         * active subfolders.
         */
        boolean hasChildren =
                folderRepository
                        .existsByParentIdAndUserEmailAndDeletedFalse(
                                folderId,
                                userEmail
                        );

        if (hasChildren) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Cannot move folder to Recycle bin because it contains subfolders"
                    );
        }

        // IMPORTANT:
        // Do NOT call folderRepository.delete().
        folder.setDeleted(true);

        folderRepository.save(folder);

        return ResponseEntity.ok(
                "Folder moved to Recycle bin"
        );
    }


    // ==========================================
    // GET RECYCLE BIN FOLDERS
    // ==========================================

    public ResponseEntity<?> getTrashFolders(
            String userEmail
    ) {

        List<Folder> folders =
                folderRepository
                        .findByUserEmailAndDeletedTrueOrderByUpdatedAtDesc(
                                userEmail
                        );

        List<FolderResponse> response =
                folders.stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // RESTORE FOLDER
    // ==========================================

    public ResponseEntity<?> restoreFolder(
            Long folderId,
            String userEmail
    ) {

        Optional<Folder> optionalFolder =
                folderRepository
                        .findByIdAndUserEmailAndDeletedTrue(
                                folderId,
                                userEmail
                        );

        if (optionalFolder.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Folder not found in Recycle bin");
        }

        Folder folder =
                optionalFolder.get();

        folder.setDeleted(false);

        folderRepository.save(folder);

        return ResponseEntity.ok(
                "Folder restored successfully"
        );
    }


    // ==========================================
    // DELETE FOLDER PERMANENTLY
    // ==========================================

    public ResponseEntity<?> permanentlyDeleteFolder(
            Long folderId,
            String userEmail
    ) {

        Optional<Folder> optionalFolder =
                folderRepository
                        .findByIdAndUserEmailAndDeletedTrue(
                                folderId,
                                userEmail
                        );

        if (optionalFolder.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Folder not found in Recycle bin");
        }

        Folder folder =
                optionalFolder.get();

        try {

            folderRepository.delete(folder);

            folderRepository.flush();

            return ResponseEntity.ok(
                    "Folder permanently deleted"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Cannot permanently delete folder because it contains files or related data"
                    );
        }
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