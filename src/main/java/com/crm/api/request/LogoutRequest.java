package com.crm.api.request;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {

    @NotBlank(message = "Refresh token is required")
    public String refreshToken;
}