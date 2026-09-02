package com.cloudstorage.repository;

import com.cloudstorage.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShareRepository
        extends JpaRepository<Share, Long> {

    Optional<Share> findByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );

    boolean existsByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );

    // ==========================================
    // SHARES CREATED BY CURRENT USER
    // ==========================================

    List<Share> findByFileUserIdAndFileDeletedFalse(
            Long userId
    );

    // ==========================================
    // FILES SHARED WITH CURRENT USER
    // ==========================================

    List<Share> findBySharedWithUserIdAndFileDeletedFalse(
            Long userId
    );
}