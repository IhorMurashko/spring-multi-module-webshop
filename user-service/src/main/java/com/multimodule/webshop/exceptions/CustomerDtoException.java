package com.multimodule.webshop.exceptions;

public class CustomerDtoException extends RuntimeException {
    public CustomerDtoException(String message) {
        super(message);
    }
}
