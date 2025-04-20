package com.multimodule.webshop.jwt;

import com.multimodule.webshop.dto.TokensDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenManager {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public TokensDto manageTokens(TokensDto tokens) {
        final String refreshToken = tokens.refreshToken();

        String newAccessToken;
        String newRefreshToken = null;

        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            newAccessToken = jwtTokenProvider.generateAccessToken(user);

            if (jwtTokenProvider.isRefreshTokenExpiredSoon(refreshToken)) {
                newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
            }
        } else {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return new TokensDto(newAccessToken,
                newRefreshToken != null
                        ? newRefreshToken
                        : tokens.refreshToken()
        );
    }


}
