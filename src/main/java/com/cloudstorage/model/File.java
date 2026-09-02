package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "files", indexes = {
        @Index(name = "idx_files_original_file_name",
                columnList = "original_file_name"),
        @Index(name = "idx_files_user_id",
                columnList = "user_id"),
        @Index(name = "idx_files_folder_id",
                columnList = "folder_id"
        )
})
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String s3Key;

    private String contentType;

    private Long size;

    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    // ==========================================
    // TRASH
    // ==========================================

    @Column(nullable = false)
    private boolean deleted = false;
}