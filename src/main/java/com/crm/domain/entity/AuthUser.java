package com.crm.domain.entity;

import com.crm.domain.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_user")
public class AuthUser extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    public UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    public String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public UserStatus status;

    @Column(name = "email_verified", nullable = false)
    public boolean emailVerified;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    public Role role;

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    @Column(name = "last_login_at")
    public OffsetDateTime lastLoginAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (status == null) {
            status = UserStatus.ACTIVE;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}