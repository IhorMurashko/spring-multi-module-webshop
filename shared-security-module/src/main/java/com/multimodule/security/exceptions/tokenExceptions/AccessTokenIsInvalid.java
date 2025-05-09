package com.multimodule.security.exceptions.tokenExceptions;

public class AccessTokenIsInvalid extends RuntimeException {
    public AccessTokenIsInvalid(String message) {
        super(message);
    }
}
