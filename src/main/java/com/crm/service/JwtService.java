package com.crm.service;

import com.crm.domain.entity.AuthUser;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "crm.jwt.access-token.duration-seconds", defaultValue = "900")
    long accessTokenDurationSeconds;

    public String generateAccessToken(AuthUser user) {
        return Jwt.issuer("crm-auth-service")
                .upn(user.email)
                .subject(user.id.toString())
                .groups(Set.of(user.role.name.name()))
                .claim("email", user.email)
                .claim("role", user.role.name.name())
                .claim("status", user.status.name())
                .expiresIn(Duration.ofSeconds(accessTokenDurationSeconds))
                .sign();
    }

    public long getAccessTokenDurationSeconds() {
        return accessTokenDurationSeconds;
    }
}