package com.cloudstorage.repository;

import com.cloudstorage.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository
        extends JpaRepository<Folder, Long> {

    // ==========================================
    // ACTIVE FOLDER
    // ==========================================

    Optional<Folder> findByIdAndUserEmailAndDeletedFalse(
            Long id,
            String email
    );


    // ==========================================
    // ROOT FOLDERS
    // ==========================================

    List<Folder> findByUserEmailAndParentIsNullAndDeletedFalseOrderByNameAsc(
            String email
    );


    // ==========================================
    // CHILD FOLDERS
    // ==========================================

    List<Folder> findByUserEmailAndParentIdAndDeletedFalseOrderByNameAsc(
            String email,
            Long parentId
    );


    // ==========================================
    // CHECK ACTIVE CHILDREN
    // ==========================================

    boolean existsByParentIdAndUserEmailAndDeletedFalse(
            Long parentId,
            String email
    );


    // ==========================================
    // RECYCLE BIN
    // ==========================================

    List<Folder> findByUserEmailAndDeletedTrueOrderByUpdatedAtDesc(
            String email
    );


    // ==========================================
    // FIND TRASH FOLDER
    // ==========================================

    Optional<Folder> findByIdAndUserEmailAndDeletedTrue(
            Long id,
            String email
    );
}