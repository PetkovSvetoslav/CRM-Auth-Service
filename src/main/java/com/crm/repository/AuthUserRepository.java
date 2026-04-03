package com.crm.repository;

import com.crm.domain.entity.AuthUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthUserRepository implements PanacheRepository<AuthUser> {

    public Optional<AuthUser> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<AuthUser> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
