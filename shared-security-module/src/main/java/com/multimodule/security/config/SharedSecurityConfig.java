package com.multimodule.security.config;

import com.multimodule.security.jwt.JwtAuthFilter;
import com.multimodule.security.jwt.JwtTokenProvider;
import com.multimodule.security.revokedTokensService.RevokedTokenService;

public interface SharedSecurityConfig {


    default JwtAuthFilter jwtAuthFilter(JwtTokenProvider jwtTokenProvider,
                                        RevokedTokenService revokedTokenService) {
        return new JwtAuthFilter(jwtTokenProvider, revokedTokenService);
    }
}
