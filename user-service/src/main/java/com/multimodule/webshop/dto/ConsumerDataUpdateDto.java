package com.multimodule.webshop.dto;

import com.multimodule.webshop.util.UserFieldConstraints;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConsumerDataUpdateDto(

        @Nullable
        @Pattern(regexp = UserFieldConstraints.EMAIL_PATTERN_CONSTRAINT,
                message = UserFieldConstraints.EMAIL_PATTERN_ERROR_MESSAGE)
        @Size(max = UserFieldConstraints.EMAIL_MAX_LENGTH_CONSTRAINT,
        message = UserFieldConstraints.EMAIL_LENGTH_ERROR_MESSAGE)
        String username,

        @Nullable
        @Size(min = UserFieldConstraints.PASSWORD_MIN_LENGTH_CONSTRAINT,
                max = UserFieldConstraints.PASSWORD_MAX_LENGTH_CONSTRAINT,
                message =UserFieldConstraints.PASSWORD_LENGTH_ERROR_MESSAGE)
        String password,

        @Nullable
        @Pattern(regexp = UserFieldConstraints.NAME_PATTERN_CONSTRAINT,
        message = UserFieldConstraints.NAME_PATTERN_ERROR_MESSAGE)
        @Size(min = UserFieldConstraints.NAME_MIN_LENGTH_CONSTRAINT,
                max = UserFieldConstraints.NAME_MAX_LENGTH_CONSTRAINT,
        message = UserFieldConstraints.NAME_LENGTH_ERROR_MESSAGE)
        String firstName,
        @Nullable
        @Pattern(regexp = UserFieldConstraints.NAME_PATTERN_CONSTRAINT,
        message = UserFieldConstraints.NAME_PATTERN_ERROR_MESSAGE)
        @Size(min = UserFieldConstraints.NAME_MIN_LENGTH_CONSTRAINT,
                max = UserFieldConstraints.NAME_MAX_LENGTH_CONSTRAINT,
        message = UserFieldConstraints.NAME_LENGTH_ERROR_MESSAGE)
        String lastName
) {
}
