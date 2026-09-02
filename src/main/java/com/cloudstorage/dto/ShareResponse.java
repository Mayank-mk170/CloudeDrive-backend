package com.cloudstorage.dto;

import com.cloudstorage.model.SharePermission;

import java.time.OffsetDateTime;

public record ShareResponse(

        Long id,

        Long fileId,

        String fileName,

        String contentType,

        Long size,

        String s3Key,

        Long sharedWithUserId,

        String sharedWithEmail,

        SharePermission permission,

        OffsetDateTime expiresAt,

        OffsetDateTime createdAt

) {
}