package com.multimodule.security.jwt.tokenManagement.managment;

import com.multimodule.security.dto.token.TokensDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface BasicTokenManager {

    TokensDto refreshToken(TokensDto tokensDto, UserDetailsService userDetailsService);

}
