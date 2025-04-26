package com.multimodule.security.config;

import com.multimodule.security.jwt.JwtAuthFilter;
import com.multimodule.security.jwt.JwtTokenProvider;
import com.multimodule.security.revokedTokensService.RevokedTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SharedSecurityConfig {


    default JwtAuthFilter jwtAuthFilter(JwtTokenProvider jwtTokenProvider,
                                       UserDetailsService userDetailsService,
                                       RevokedTokenService revokedTokenService) {
        return new JwtAuthFilter(jwtTokenProvider, userDetailsService, revokedTokenService);
    }
}
