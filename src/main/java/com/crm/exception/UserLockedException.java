package com.crm.exception;

public class UserLockedException extends RuntimeException {
    public UserLockedException(String message) {
        super(message);
    }
}