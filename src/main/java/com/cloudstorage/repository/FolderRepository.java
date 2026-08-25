package com.cloudstorage.repository;

import com.cloudstorage.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    // Find a folder only if it belongs to the logged-in user
    Optional<Folder> findByIdAndUserEmail(
            Long id,
            String email
    );

    // Get root folders of a user
    List<Folder> findByUserEmailAndParentIsNullOrderByNameAsc(
            String email
    );

    // Get child folders inside a parent folder
    List<Folder> findByUserEmailAndParentIdOrderByNameAsc(
            String email,
            Long parentId
    );

}
