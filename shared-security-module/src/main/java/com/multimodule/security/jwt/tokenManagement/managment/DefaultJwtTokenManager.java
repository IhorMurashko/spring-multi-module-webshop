package com.multimodule.security.jwt.tokenManagement.managment;


import com.multimodule.security.exceptions.ExceptionsConstantsMessage;
import com.multimodule.security.exceptions.UserDetailsInstanceOfException;
import com.multimodule.security.exceptions.tokenExceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.security.userDetails.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
public class DefaultJwtTokenManager implements BasicTokenManager {

    private final BasicJwtTokenProvider defaultJwtTokenProvider;

    public TokensDto refreshToken(TokensDto tokens, UserDetailsService userDetailsService) {

        final String refreshToken = tokens.refreshToken();

        String newAccessToken;
        String newRefreshToken = null;

        if (defaultJwtTokenProvider.validateToken(refreshToken)) {

            String username = defaultJwtTokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            JwtPrincipal jwtPrincipal = fromUserDetails(userDetails);

            newAccessToken = delegateGenerateNewAccessToken(jwtPrincipal);

            if (checkIfNeedNewRefreshToken(refreshToken)) {
                newRefreshToken = delegateGenerateNewRefreshToken(jwtPrincipal);
            }

            return new TokensDto(newAccessToken,
                    newRefreshToken != null
                            ? newRefreshToken
                            : tokens.refreshToken()
            );
        } else {
            log.error("Refresh token is invalid for user: {} ",
                    defaultJwtTokenProvider.getUsernameFromToken(refreshToken));

            throw new RefreshingTokenIsInvalidException(
                    ExceptionsConstantsMessage.REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE);
        }


    }

    private String delegateGenerateNewAccessToken(JwtPrincipal userDetails) {
        return defaultJwtTokenProvider.generateAccessToken(userDetails, 900_000L);
    }

    private boolean checkIfNeedNewRefreshToken(String refreshToken) {
        //24 hours
        return defaultJwtTokenProvider.isTokenExpiredSoon(refreshToken, 86400000L);
    }

    private String delegateGenerateNewRefreshToken(JwtPrincipal userDetails) {
        return defaultJwtTokenProvider.generateRefreshToken(userDetails, 2592000000L);
    }

    public static JwtPrincipal fromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal;
        }
        throw new UserDetailsInstanceOfException("UserDetails must implement JwtPrincipal");
    }

}
