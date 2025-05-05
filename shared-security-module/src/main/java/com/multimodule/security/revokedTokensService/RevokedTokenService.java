package com.multimodule.security.revokedTokensService;

import com.multimodule.security.dtos.token.TokensDto;

public interface RevokedTokenService {

    /**
     * Revokes the provided tokens by storing them in Redis with a TTL equal to their remaining lifetime.
     * <p>
     * The method expects a map containing the keys "refreshToken" and "accessToken". For each token, it calculates
     * the remaining time until expiration and stores the token in Redis with that TTL if the token is still valid.
     *
     * @param tokens a object containing tokens value (e.g., accessToken, refreshToken)
     */
    void revokeToken(TokensDto tokens);

    /**
     * Checks if a given token is revoked by verifying its existence in the Redis blacklist.
     *
     * @param token the JWT token to check for revocation
     * @return {@code true} if the token is revoked; {@code false} otherwise
     */
    boolean isTokenRevoked(String token);

}
