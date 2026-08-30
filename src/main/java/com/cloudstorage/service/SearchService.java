package com.cloudstorage.service;

import com.cloudstorage.model.File;
import com.cloudstorage.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final FileRepository fileRepository;

    public SearchService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Page<File> searchFiles(
            String userEmail,
            String name,
            int page,
            int size
    ) {

        // Prevent invalid pagination values
        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        // Maximum page size
        if (size > 100) {
            size = 100;
        }

        Pageable pageable =
                PageRequest.of(page, size);

        return fileRepository
                .findByUserEmailAndOriginalFileNameContainingIgnoreCase(
                        userEmail,
                        name == null ? "" : name,
                        pageable
                );
    }
}