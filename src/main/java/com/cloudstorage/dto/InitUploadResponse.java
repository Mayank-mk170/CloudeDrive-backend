package com.cloudstorage.dto;

public record InitUploadResponse(
        Long fileId,

        String uploadUrl,

        String s3Key
) {
}
