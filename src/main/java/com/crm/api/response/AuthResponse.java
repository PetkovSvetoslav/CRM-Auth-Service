package com.crm.api.response;

public class AuthResponse {
    public String accessToken;
    public String refreshToken;
    public String tokenType;
    public long expiresIn;
    public UserResponse user;
}