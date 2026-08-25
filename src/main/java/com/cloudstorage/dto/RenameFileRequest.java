package com.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameFileRequest(
        @NotBlank(message = "File name is required")
        @Size(
                max = 255,
                message = "File name cannot exceed 255 characters"
        )
        String fileName
) {
}
