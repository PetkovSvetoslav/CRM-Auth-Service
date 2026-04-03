package com.crm.repository;

import com.crm.domain.entity.Role;
import com.crm.domain.enums.RoleType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<Role> {

    public Optional<Role> findByName(RoleType roleType) {
        return find("name", roleType).firstResultOptional();
    }
}