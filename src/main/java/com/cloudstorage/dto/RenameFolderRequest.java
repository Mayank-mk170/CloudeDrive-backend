package com.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameFolderRequest(

        @NotBlank(message = "Folder name is required")
        @Size(max = 255, message = "Folder name must not exceed 255 characters")
        String name

) {
}