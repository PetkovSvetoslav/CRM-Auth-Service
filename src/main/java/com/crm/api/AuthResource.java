package com.crm.api;

import com.crm.api.request.LoginRequest;
import com.crm.api.request.LogoutRequest;
import com.crm.api.request.RefreshTokenRequest;
import com.crm.api.request.RegisterRequest;
import com.crm.api.response.*;
import com.crm.security.AuthenticatedUserProvider;
import com.crm.service.AuthService;
import com.crm.util.TimeUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    AuthenticatedUserProvider authenticatedUserProvider;

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        UserResponse userResponse = authService.register(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(userResponse, new MetaResponse(TimeUtil.nowIso()));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        ApiResponse<AuthResponse> response = new ApiResponse<>(authResponse, new MetaResponse(TimeUtil.nowIso()));
        return Response.ok(response).build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@Valid RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refresh(request.refreshToken);
        ApiResponse<AuthResponse> response = new ApiResponse<>(authResponse, new MetaResponse(TimeUtil.nowIso()));
        return Response.ok(response).build();
    }

    @POST
    @Path("/logout")
    @PermitAll
    public Response logout(@Valid LogoutRequest request) {
        MessageResponse messageResponse = authService.logout(request.refreshToken);
        ApiResponse<MessageResponse> response = new ApiResponse<>(messageResponse, new MetaResponse(TimeUtil.nowIso()));
        return Response.ok(response).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "SALES_MANAGER", "SALES_REP", "SUPPORT_AGENT"})
    public Response me() {
        UserResponse userResponse = authService.me(authenticatedUserProvider.getCurrentUser());
        ApiResponse<UserResponse> response = new ApiResponse<>(userResponse, new MetaResponse(TimeUtil.nowIso()));
        return Response.ok(response).build();
    }
}