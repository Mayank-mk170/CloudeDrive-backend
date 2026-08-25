package com.cloudstorage.dto;

import java.time.OffsetDateTime;

public record PublicLinkRequest(
        Long fileId,
        OffsetDateTime expiresAt
) {
}
