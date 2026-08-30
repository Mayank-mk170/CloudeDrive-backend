package com.cloudstorage.repository;

import com.cloudstorage.model.Star;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Long> {

    boolean existsByUserIdAndFileId(
            Long userId,
            Long fileId
    );

    Optional<Star> findByUserIdAndFileId(
            Long userId,
            Long fileId
    );

    List<Star> findByUserIdAndFileDeletedFalse(
            Long userId
    );
}