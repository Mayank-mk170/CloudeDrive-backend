package com.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InitUploadRequest(
        @NotBlank
        String fileName,

        @NotBlank
        String contentType,

        @NotNull
        Long size
) {
}
