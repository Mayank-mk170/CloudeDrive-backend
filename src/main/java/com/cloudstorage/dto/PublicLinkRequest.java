package com.cloudstorage.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record PublicLinkRequest(
        @NotNull(message = "File ID is required")
        Long fileId,

        OffsetDateTime expiresAt
) {
}
