package com.multimodule.webshop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFieldConstraints {

    public static final String EMAIL_PATTERN_CONSTRAINT = "^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$";
    public static final int EMAIL_MAX_LENGTH_CONSTRAINT = 50;
    public static final String EMAIL_PATTERN_ERROR_MESSAGE = "Email format address is incorrect";
    public static final String EMAIL_LENGTH_ERROR_MESSAGE = "Email must not exceed 50 characters"
            ;

    public static final int PASSWORD_MIN_LENGTH_CONSTRAINT = 6;
    public static final int PASSWORD_MAX_LENGTH_CONSTRAINT = 60;
    public static final String PASSWORD_LENGTH_ERROR_MESSAGE = "Length of the password must be between 6-60 characters";


    public static final String NAME_PATTERN_CONSTRAINT = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$";
    public static final String NAME_PATTERN_ERROR_MESSAGE = "Name must consist of letters only, " +
            "optionally separated by a space or hyphen";

    public static final int NAME_MIN_LENGTH_CONSTRAINT = 2;
    public static final int NAME_MAX_LENGTH_CONSTRAINT = 60;
    public static final String NAME_LENGTH_ERROR_MESSAGE = "Name must be between 2 and 60 characters long";


}
