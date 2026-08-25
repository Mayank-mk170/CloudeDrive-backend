package com.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  CreateFolderRequest(
        @NotBlank(message = "Folder name is required")
        @Size(
                max = 255,
                message = "Folder name cannot exceed 255 characters"
        )
        String name,

        Long parentId
) {
}
