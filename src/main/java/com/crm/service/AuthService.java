package com.crm.service;

import com.crm.api.mapper.AuthApiMapper;
import com.crm.api.request.LoginRequest;
import com.crm.api.request.RegisterRequest;
import com.crm.api.response.AuthResponse;
import com.crm.api.response.MessageResponse;
import com.crm.api.response.UserResponse;
import com.crm.domain.entity.AuthUser;
import com.crm.domain.entity.RefreshToken;
import com.crm.domain.entity.Role;
import com.crm.domain.enums.RoleType;
import com.crm.domain.enums.UserStatus;
import com.crm.exception.EmailAlreadyExistsException;
import com.crm.exception.InvalidCredentialsException;
import com.crm.exception.NotFoundException;
import com.crm.exception.PasswordMismatchException;
import com.crm.exception.UserLockedException;
import com.crm.repository.AuthUserRepository;
import com.crm.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthUserRepository authUserRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    PasswordService passwordService;

    @Inject
    JwtService jwtService;

    @Inject
    RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email)) {
            throw new EmailAlreadyExistsException("A user with this email already exists");
        }

        if (!request.password.equals(request.confirmPassword)) {
            throw new PasswordMismatchException("Password and confirm password do not match");
        }

        Role defaultRole = roleRepository.findByName(RoleType.SALES_REP)
                .orElseThrow(() -> new NotFoundException("Default role SALES_REP not found"));

        AuthUser user = new AuthUser();
        user.id = UUID.randomUUID();
        user.email = request.email.trim().toLowerCase();
        user.passwordHash = passwordService.hashPassword(request.password);
        user.status = UserStatus.ACTIVE;
        user.emailVerified = false;
        user.role = defaultRole;
        user.createdAt = OffsetDateTime.now();
        user.updatedAt = OffsetDateTime.now();

        authUserRepository.persist(user);

        return AuthApiMapper.toUserResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.status == UserStatus.LOCKED) {
            throw new UserLockedException("User account is locked");
        }

        if (user.status != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!passwordService.matches(request.password, user.passwordHash)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        user.lastLoginAt = OffsetDateTime.now();

        RefreshToken refreshToken = refreshTokenService.create(user);
        String accessToken = jwtService.generateAccessToken(user);

        AuthResponse response = new AuthResponse();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken.token;
        response.tokenType = "Bearer";
        response.expiresIn = jwtService.getAccessTokenDurationSeconds();
        response.user = AuthApiMapper.toUserResponse(user);

        return response;
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateAndGet(refreshTokenValue);
        AuthUser user = refreshToken.user;

        if (user.status == UserStatus.LOCKED) {
            throw new UserLockedException("User account is locked");
        }

        if (user.status != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("User is not active");
        }

        refreshToken.revoked = true;
        RefreshToken newRefreshToken = refreshTokenService.create(user);
        String accessToken = jwtService.generateAccessToken(user);

        AuthResponse response = new AuthResponse();
        response.accessToken = accessToken;
        response.refreshToken = newRefreshToken.token;
        response.tokenType = "Bearer";
        response.expiresIn = jwtService.getAccessTokenDurationSeconds();
        response.user = AuthApiMapper.toUserResponse(user);

        return response;
    }

    @Transactional
    public MessageResponse logout(String refreshTokenValue) {
        refreshTokenService.revoke(refreshTokenValue);
        return new MessageResponse("Logout successful");
    }

    public UserResponse me(AuthUser user) {
        return AuthApiMapper.toUserResponse(user);
    }

    public AuthUser getUserById(UUID userId) {
        return authUserRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}