package com.cloudstorage.dto;

import com.cloudstorage.model.SharePermission;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ShareRequest(
        @NotNull(message = "File ID is required")
        Long fileId,

        @NotNull(message = "Shared user ID is required")
        Long sharedWithUserId,

        @NotNull(message = "Permission is required")
        SharePermission permission,

        OffsetDateTime expiresAt
) {
}
