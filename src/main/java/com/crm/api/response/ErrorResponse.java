package com.crm.api.response;

public class ErrorResponse {

    public ErrorBody error;
    public MetaResponse meta;

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorBody error, MetaResponse meta) {
        this.error = error;
        this.meta = meta;
    }
}