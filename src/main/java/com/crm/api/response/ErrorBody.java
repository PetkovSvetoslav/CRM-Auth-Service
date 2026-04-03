package com.crm.api.response;

import java.util.List;

public class ErrorBody {
    public String code;
    public String message;
    public List<ValidationErrorDetail> details;

    public ErrorBody() {
    }

    public ErrorBody(String code, String message, List<ValidationErrorDetail> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}