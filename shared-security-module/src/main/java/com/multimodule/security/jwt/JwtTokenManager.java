package com.multimodule.security.jwt;


import com.multimodule.security.dtos.TokensDto;
import com.multimodule.security.exceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.exceptions.UserDetailsInstanceOfException;
import com.multimodule.security.userDetails.JwtPrincipal;
import com.multimodule.security.utils.CustomExceptionsMessage;
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

    public TokensDto manageTokens(TokensDto tokens, UserDetailsService userDetailsService) {

        final String refreshToken = tokens.refreshToken();

        String newAccessToken;
        String newRefreshToken = null;

        if (jwtTokenProvider.validateToken(refreshToken)) {

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            JwtPrincipal jwtPrincipal = fromUserDetails(userDetails);

            newAccessToken = delegateGenerateNewAccessToken(jwtPrincipal);

            if (checkIfNeedNewRefresh(refreshToken)) {
                newRefreshToken = delegateGenerateNewRefreshToken(jwtPrincipal);
            }

            return new TokensDto(newAccessToken,
                    newRefreshToken != null
                            ? newRefreshToken
                            : tokens.refreshToken()
            );
        } else {
            log.error("Refresh token is invalid for user: {} ",
                    jwtTokenProvider.getUsernameFromToken(refreshToken));

            throw new RefreshingTokenIsInvalidException(
                    CustomExceptionsMessage.REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE);
        }


    }

    private String delegateGenerateNewAccessToken(JwtPrincipal userDetails) {
        return jwtTokenProvider.generateAccessToken(userDetails);
    }

    private boolean checkIfNeedNewRefresh(String refreshToken) {
        return jwtTokenProvider.isRefreshTokenExpiredSoon(refreshToken);
    }

    private String delegateGenerateNewRefreshToken(JwtPrincipal userDetails) {
        return jwtTokenProvider.generateRefreshToken(userDetails);
    }

    public static JwtPrincipal fromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal;
        }
        throw new UserDetailsInstanceOfException("UserDetails must implement JwtPrincipal");
    }

}
