package com.cloudstorage.dto;

import java.time.OffsetDateTime;

public record FileResponse(
        Long id,
        String originalFileName,
        String storedFileName,
        String contentType,
        Long size,
        String s3Key,
        OffsetDateTime createdAt
) {
}
