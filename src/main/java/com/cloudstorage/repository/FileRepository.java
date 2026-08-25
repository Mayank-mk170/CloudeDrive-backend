package com.cloudstorage.repository;

import com.cloudstorage.model.File;
import com.cloudstorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUser(User user);
    Optional<File> findByIdAndUserEmail(
            Long id, String email);
}
