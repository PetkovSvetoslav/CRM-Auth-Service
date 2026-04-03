package com.crm.api.mapper;

import com.crm.api.response.UserResponse;
import com.crm.domain.entity.AuthUser;

public final class AuthApiMapper {

    private AuthApiMapper() {
    }

    public static UserResponse toUserResponse(AuthUser user) {
        UserResponse response = new UserResponse();
        response.id = user.id != null ? user.id.toString() : null;
        response.email = user.email;
        response.role = user.role != null && user.role.name != null ? user.role.name.name() : null;
        response.status = user.status != null ? user.status.name() : null;
        response.emailVerified = user.emailVerified;
        return response;
    }
}