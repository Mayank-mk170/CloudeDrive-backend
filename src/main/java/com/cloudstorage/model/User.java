package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_users_provider_id",
                        columnNames = "provider_id"
                )
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Password can be NULL for Google OAuth users
    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

}