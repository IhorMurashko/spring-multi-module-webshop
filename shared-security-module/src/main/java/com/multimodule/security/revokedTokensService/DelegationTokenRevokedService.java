package com.multimodule.security.revokedTokensService;

import com.multimodule.security.dto.token.TokensDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelegationTokenRevokedService implements BasicRevokedTokenService {

    private final TokenCacheService tokenCacheService;

    @Override
    public void revokeToken(TokensDto tokens) {
        tokenCacheService.saveToken(tokens);
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return tokenCacheService.isTokenPresent(token);
    }
}
