package com.crm.service;

import com.crm.domain.entity.AuthUser;
import com.crm.domain.entity.RefreshToken;
import com.crm.exception.InvalidRefreshTokenException;
import com.crm.repository.RefreshTokenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenService {

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @ConfigProperty(name = "crm.jwt.refresh-token.duration-seconds", defaultValue = "604800")
    long refreshTokenDurationSeconds;

    @Transactional
    public RefreshToken create(AuthUser user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.user = user;
        refreshToken.token = UUID.randomUUID().toString();
        refreshToken.expiresAt = OffsetDateTime.now().plusSeconds(refreshTokenDurationSeconds);
        refreshToken.revoked = false;

        refreshTokenRepository.persist(refreshToken);
        return refreshToken;
    }

    public RefreshToken validateAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        if (refreshToken.revoked || refreshToken.expiresAt.isBefore(OffsetDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoked = true;
        });
    }

    @Transactional
    public void revokeAllActiveTokens(AuthUser user) {
        List<RefreshToken> tokens = refreshTokenRepository.findActiveTokensByUser(user);
        for (RefreshToken token : tokens) {
            token.revoked = true;
        }
    }
}