package com.cloudstorage.repository;

import com.cloudstorage.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {
    Optional<Share> findByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );

    boolean existsByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );
}
