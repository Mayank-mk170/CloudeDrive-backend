package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "folders",  indexes = {
        @Index(name = "idx_folder_user", columnList = "user_id"),
        @Index(name = "idx_folder_parent", columnList = "parent_id")})
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @Column(
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // ==========================================
    // TRASH
    // ==========================================

    @Column(nullable = false)
    private boolean deleted = false;


    // ==========================================
    // CREATE
    // ==========================================

    @PrePersist
    protected void onCreate() {

        OffsetDateTime now =
                OffsetDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    // ==========================================
    // UPDATE
    // ==========================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                OffsetDateTime.now();
    }
}