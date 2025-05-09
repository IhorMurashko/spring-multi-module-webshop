package com.multimodule.security.revokedTokensService;

import com.multimodule.security.dto.token.TokensDto;

public interface TokenCacheService {

    void saveToken(TokensDto tokens);

    boolean isTokenPresent(String token);

}
