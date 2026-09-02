package com.cloudstorage.dto;

import com.cloudstorage.model.SharePermission;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ShareRequest(

        @NotNull(message = "File ID is required")
        Long fileId,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,

        @NotNull(message = "Permission is required")
        SharePermission permission,

        OffsetDateTime expiresAt
) {
}