package com.cloudstorage.dto;

import java.time.OffsetDateTime;

public record FolderResponse(
        Long id,
        String name,
        Long parentId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
