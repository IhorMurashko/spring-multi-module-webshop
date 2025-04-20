package com.multimodule.webshop.dto;

import com.multimodule.webshop.util.UserFieldConstraints;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record AuthCredentials(
        @NotNull
        @Email(regexp = UserFieldConstraints.EMAIL_PATTERN_CONSTRAINT,
                message = UserFieldConstraints.EMAIL_PATTERN_ERROR_MESSAGE)
        @Size(max = UserFieldConstraints.EMAIL_MAX_LENGTH_CONSTRAINT,
                message = UserFieldConstraints.EMAIL_LENGTH_ERROR_MESSAGE)
        String username,

        @NotNull
        @Size(min = UserFieldConstraints.PASSWORD_MIN_LENGTH_CONSTRAINT,
                max = UserFieldConstraints.PASSWORD_MAX_LENGTH_CONSTRAINT,
                message = UserFieldConstraints.PASSWORD_LENGTH_ERROR_MESSAGE)
        String password
) implements Serializable { }
