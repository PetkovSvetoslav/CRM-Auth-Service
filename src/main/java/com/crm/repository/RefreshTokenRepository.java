package com.crm.repository;

import com.crm.domain.entity.AuthUser;
import com.crm.domain.entity.RefreshToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepository<RefreshToken> {

    public Optional<RefreshToken> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public List<RefreshToken> findActiveTokensByUser(AuthUser user) {
        return list("user = ?1 and revoked = false and expiresAt > ?2", user, OffsetDateTime.now());
    }

    public Optional<RefreshToken> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}