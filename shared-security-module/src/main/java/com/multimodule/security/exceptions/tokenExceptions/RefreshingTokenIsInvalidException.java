package com.multimodule.security.exceptions.tokenExceptions;

public class RefreshingTokenIsInvalidException extends RuntimeException {
    public RefreshingTokenIsInvalidException(String message) {
        super(message);
    }
}
