package com.multimodule.webshop.exceptions;

public class ConsumerUsernameNotFoundException extends RuntimeException {
    public ConsumerUsernameNotFoundException(String message) {
        super(message);
    }
}
