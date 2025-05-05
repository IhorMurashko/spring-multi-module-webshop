package com.multimodule.security.exceptions.gateway;

public class HeaderDoesntContainsCredentialsException extends RuntimeException {

    public HeaderDoesntContainsCredentialsException(String message) {
        super(message);
    }
}
