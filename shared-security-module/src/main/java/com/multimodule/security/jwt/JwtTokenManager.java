package com.multimodule.security.jwt;


import com.multimodule.security.dtos.TokensDto;
import com.multimodule.security.exceptions.RefreshingTokenIsInvalidException;
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

            newAccessToken = generateNewAccessToken(refreshToken, userDetailsService);

            if (checkIfNeedNewRefresh(refreshToken)) {
                newRefreshToken = generateNewRefreshToken(refreshToken, userDetailsService);
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

    private String generateNewAccessToken(String refreshToken, UserDetailsService userDetailsService) {


        String usernameFromToken = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);

        return jwtTokenProvider.generateAccessToken(userDetails);


    }

    private boolean checkIfNeedNewRefresh(String refreshToken) {
        return jwtTokenProvider.isRefreshTokenExpiredSoon(refreshToken);
    }

    private String generateNewRefreshToken(String refreshToken, UserDetailsService userDetailsService) {

        String usernameFromToken = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);
        return jwtTokenProvider.generateRefreshToken(userDetails);

    }


}
