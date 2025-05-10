package com.multimodule.security.jwt.tokenManagement.managment;


import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.security.exceptions.ExceptionConstantMessage;
import com.multimodule.security.exceptions.tokenExceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.exceptions.userDetails.UserDetailsInstanceOfException;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.userDetails.JwtPrincipal;
import com.multimodule.security.userDetailsService.TypedUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RequiredArgsConstructor
@Slf4j
public class DefaultJwtTokenManager implements BasicTokenManager {

    private final BasicJwtTokenProvider jwtTokenProvider;
    private final TypedUserDetailsService<? extends UserDetailsService> userDetailsService;

    public TokensDto refreshToken(TokensDto tokens) {

        final String refreshToken = tokens.refreshToken();

        String newAccessToken;
        String newRefreshToken = null;

        if (jwtTokenProvider.validateToken(refreshToken)) {

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
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
                    jwtTokenProvider.getUsernameFromToken(refreshToken));

            throw new RefreshingTokenIsInvalidException(
                    ExceptionConstantMessage.REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE);
        }


    }

    private String delegateGenerateNewAccessToken(JwtPrincipal userDetails) {
        //15 minutes
        return jwtTokenProvider.generateAccessToken(userDetails, 900_000L);
    }

    private boolean checkIfNeedNewRefreshToken(String refreshToken) {
        //24 hours
        return jwtTokenProvider.isTokenExpiredSoon(refreshToken, 86400000L);
    }

    private String delegateGenerateNewRefreshToken(JwtPrincipal userDetails) {
        //30 days
        return jwtTokenProvider.generateRefreshToken(userDetails, 2592000000L);
    }

    public static JwtPrincipal fromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal;
        }
        throw new UserDetailsInstanceOfException("UserDetails must implement JwtPrincipal");
    }

}
