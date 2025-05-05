package com.multimodule.security.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomExceptionsMessage {

    public final static String REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE = "Refreshing token is invalid";
    public final static String REFRESHING_TOKEN_CANT_BE_USED_FOR_AUTHENTICATION_EXCEPTION_MESSAGE = "Refreshing token can't be used for authentication";
    public final static String ACCESS_DENIED_EXCEPTION_MESSAGE = "Error: Access denied";
    public final static String UNAUTHORIZED_EXCEPTION_MESSAGE = "Error: Unauthorized";
    public final static String TOKEN_HAS_BEEN_REVOKED_EXCEPTION_MESSAGE = "Token has been revoked";


}
