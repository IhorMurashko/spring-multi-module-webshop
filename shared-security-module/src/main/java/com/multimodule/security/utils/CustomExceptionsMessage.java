package com.multimodule.security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomExceptionsMessage {

    public final static String REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE = "Refreshing token is invalid";
    public final static String ACCESS_DENIED_EXCEPTION_MESSAGE = "Error: Access denied";
    public final static String UNAUTHORIZED_EXCEPTION_MESSAGE = "Error: Unauthorized";


}
