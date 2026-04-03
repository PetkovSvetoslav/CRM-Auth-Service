package com.crm.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public String hashPassword(String rawPassword) {
        return BcryptUtil.bcryptHash(rawPassword);
    }

    public boolean matches(String rawPassword, String passwordHash) {
        return BcryptUtil.matches(rawPassword, passwordHash);
    }
}