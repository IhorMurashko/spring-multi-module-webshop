package com.multimodule.webshop.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
