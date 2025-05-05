package com.multimodule.security.exceptions.tokenExceptions;

public class RevokedTokenException extends RuntimeException {
    public RevokedTokenException(String message) {
        super(message);
    }
}
