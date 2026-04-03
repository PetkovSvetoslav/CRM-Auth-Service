package com.crm.security;

import com.crm.domain.entity.AuthUser;
import com.crm.exception.NotFoundException;
import com.crm.repository.AuthUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
public class AuthenticatedUserProvider {

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    AuthUserRepository authUserRepository;

    public AuthUser getCurrentUser() {
        if (jsonWebToken == null || jsonWebToken.getSubject() == null) {
            throw new NotFoundException("Authenticated user not found");
        }

        UUID userId = UUID.fromString(jsonWebToken.getSubject());

        return authUserRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
    }
}