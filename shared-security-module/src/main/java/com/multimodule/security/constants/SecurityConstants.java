package com.multimodule.security.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    // === JWT ===
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    public static final String TOKEN_TYPE_CLAIM = "type";
    public static final String TOKEN_ID_CLAIM = "tokenId";

    // === CLAIMS ===
    public static final String USER_ID_CLAIM = "userId";
    public static final String USERNAME_CLAIM = "username";
    public static final String ROLES_CLAIM = "roles";

    // === Headers (в проксировании через Gateway) ===
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_ROLES = "X-Roles";
    public static final String HEADER_TOKEN_ID = "X-Token-Id";

    // === Internal communication ===
    public static final String GATEWAY_INTERNAL_HEADER = "X-Gateway-Secret"; // например, можно проверять по значению
    public static final String GATEWAY_SECRET_VALUE = "SuperSecureInternalSecret"; // лучше хранить в конфиге

}
