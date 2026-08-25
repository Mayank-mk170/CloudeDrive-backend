package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "shares", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_file_shared_user",
                columnNames = {"file_id", "shared_with_user_id"}
        )
})
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // File being shared
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_id",
            nullable = false
    )
    private File file;

    // User receiving the share
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_with_user_id",
            nullable = false
    )
    private User sharedWithUser;

    // VIEW or EDIT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SharePermission permission;

    // Optional expiry
    private OffsetDateTime expiresAt;

    private OffsetDateTime createdAt;
}