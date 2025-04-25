package com.multimodule.security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomExceptionsMessage {

    public final static String REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE = "Refreshing token is invalid";
}
