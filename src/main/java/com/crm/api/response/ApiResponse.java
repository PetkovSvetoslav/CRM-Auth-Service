package com.crm.api.response;

public class ApiResponse<T> {
    public T data;
    public MetaResponse meta;

    public ApiResponse() {
    }

    public ApiResponse(T data, MetaResponse meta) {
        this.data = data;
        this.meta = meta;
    }
}