package com.crm.exception;

import com.crm.api.response.ErrorBody;
import com.crm.api.response.ErrorResponse;
import com.crm.api.response.MetaResponse;
import com.crm.api.response.ValidationErrorDetail;
import com.crm.util.TimeUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof EmailAlreadyExistsException ex) {
            return build(Response.Status.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage(), List.of());
        }

        if (exception instanceof InvalidCredentialsException ex) {
            return build(Response.Status.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage(), List.of());
        }

        if (exception instanceof InvalidRefreshTokenException ex) {
            return build(Response.Status.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", ex.getMessage(), List.of());
        }

        if (exception instanceof UserLockedException ex) {
            return build(Response.Status.FORBIDDEN, "USER_LOCKED", ex.getMessage(), List.of());
        }

        if (exception instanceof PasswordMismatchException ex) {
            return build(Response.Status.BAD_REQUEST, "PASSWORD_MISMATCH", ex.getMessage(), List.of());
        }

        if (exception instanceof NotFoundException ex) {
            return build(Response.Status.NOT_FOUND, "NOT_FOUND", ex.getMessage(), List.of());
        }

        if (exception instanceof ConstraintViolationException ex) {
            List<ValidationErrorDetail> details = ex.getConstraintViolations()
                    .stream()
                    .map(this::toDetail)
                    .toList();

            return build(Response.Status.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details);
        }

        return build(Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Unexpected server error", List.of());
    }

    private ValidationErrorDetail toDetail(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "unknown";
        String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        return new ValidationErrorDetail(field, violation.getMessage());
    }

    private Response build(Response.Status status, String code, String message, List<ValidationErrorDetail> details) {
        ErrorBody errorBody = new ErrorBody(code, message, details);
        ErrorResponse errorResponse = new ErrorResponse(errorBody, new MetaResponse(TimeUtil.nowIso()));

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}