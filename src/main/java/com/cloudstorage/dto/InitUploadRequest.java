package com.cloudstorage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InitUploadRequest(
        @NotBlank(message = "File name is required")
        @Size(
                max = 255,
                message = "File name cannot exceed 255 characters"
        )
        String fileName,

        @NotBlank(message = "Content type is required")
        @Size(
                max = 100,
                message = "Content type cannot exceed 100 characters"
        )
        String contentType,

        @NotNull(message = "File size is required")
        @Min(
                value = 1,
                message = "File size must be greater than 0"
        )
        Long size
) {
}
