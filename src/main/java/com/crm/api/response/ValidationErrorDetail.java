package com.crm.api.response;

public class ValidationErrorDetail {
    public String field;
    public String message;

    public ValidationErrorDetail() {
    }

    public ValidationErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }
}