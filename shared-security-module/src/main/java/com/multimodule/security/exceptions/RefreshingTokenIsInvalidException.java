package com.multimodule.security.exceptions;

public class RefreshingTokenIsInvalidException extends RuntimeException {
    public RefreshingTokenIsInvalidException(String message) {
        super(message);
    }
}
