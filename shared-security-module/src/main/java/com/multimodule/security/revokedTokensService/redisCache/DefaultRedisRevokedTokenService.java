package com.multimodule.security.revokedTokensService.redisCache;

import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.revokedTokensService.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@ConditionalOnMissingBean
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultRedisRevokedTokenService implements TokenCacheService {


    /**
     * Redis template for performing string-based operations.
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * JwtTokenProvider provides methods for validating and extracting claims from JWT tokens.
     */
    private final BasicJwtTokenProvider defaultJwtTokenProvider;


    @Override
    public void saveToken(TokensDto tokens) {

        final String refreshToken = tokens.refreshToken();
        final String accessToken = tokens.accessToken();

        // Revoke access token
        if (accessToken != null) {
            long accessTokenExpiration = defaultJwtTokenProvider.extractClaimFromToken(accessToken,
                    claims -> claims.getExpiration().getTime());
            long accessTokenTTL = accessTokenExpiration - System.currentTimeMillis();
            if (accessTokenTTL > 0) {
                redisTemplate.opsForValue().set(accessToken, "access_token_revoked",
                        accessTokenTTL, TimeUnit.MILLISECONDS);
                log.info("Revoked access token with TTL: {} ms", accessTokenTTL);
            }
        } else {
            log.warn("Access token is missing from the revoke request");
        }

        // Revoke refresh token
        if (refreshToken != null) {
            long refreshTokenExpiration = defaultJwtTokenProvider.extractClaimFromToken(refreshToken, claims ->
                    claims.getExpiration().getTime());
            long refreshTokenTTL = refreshTokenExpiration - System.currentTimeMillis();
            if (refreshTokenTTL > 0) {
                redisTemplate.opsForValue().set(refreshToken, "refresh_token_revoked", refreshTokenTTL, TimeUnit.MILLISECONDS);
                log.info("Revoked refresh token with TTL: {} ms", refreshTokenTTL);
            }
        } else {
            log.warn("Refresh token is missing from the revoke request");
        }

    }

    @Override
    public boolean isTokenPresent(String token) {
        return redisTemplate.hasKey(token);
    }
}
