package com.cloudstorage.repository;

import com.cloudstorage.model.PublicLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicLinkRepository extends JpaRepository<PublicLink, Long> {
    Optional<PublicLink> findByToken(String token);

    boolean existsByFileId(Long fileId);
}
