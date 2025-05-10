package com.multimodule.security.jwt.tokenManagement.managment;

import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.security.exceptions.ExceptionConstantMessage;
import com.multimodule.security.exceptions.tokenExceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.roles.Roles;
import com.multimodule.security.userDetails.JwtPrincipal;
import com.multimodule.security.userDetails.JwtUserPrincipal;
import com.multimodule.security.userDetailsService.TypedUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultJwtTokenManagerTest {


    @Mock
    private BasicJwtTokenProvider tokenProvider;
    @Mock
    private TypedUserDetailsService<? extends UserDetailsService> userDetailsService;

    @InjectMocks
    private DefaultJwtTokenManager defaultJwtTokenManager;


    @Captor
    private ArgumentCaptor<String> tokenCaptor;
    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    private TokensDto tokensDto;
    private String username;
    private JwtPrincipal jwtPrincipal;
    private String accessToken;
    private String refreshToken;
    private String newAccessToken;
    private String newRefreshToken;
    private long userId;
    private JwtUserPrincipal jwtUserPrincipal;
    private long expirationDateLess24hInMs;
    private long accessTokenLifeTime;
    private long refreshTokenLifeTime;


    @BeforeEach
    void setUp() {
        this.accessToken = "accessToken";
        this.refreshToken = "refreshToken";
        this.tokensDto = new TokensDto(accessToken, refreshToken);
        this.username = "username";
        this.jwtPrincipal = new JwtUserPrincipal(1L, new ArrayList<GrantedAuthority>(
                Collections.singleton(Roles.ROLE_USER)),
                "password", username, true, true,
                true, true);
        this.newAccessToken = "newAccessToken";
        this.newRefreshToken = "newRefreshToken";

        this.userId = 1L;
        this.jwtUserPrincipal = new JwtUserPrincipal(
                userId,
                List.of(Roles.ROLE_USER),
                "password",
                username,
                true, true,
                true, true
        );

        this.expirationDateLess24hInMs = 86400000L;
        this.accessTokenLifeTime = 900_000L;
        this.refreshTokenLifeTime = 2592000000L;
    }


    @Test()
    void getRefreshingTokenIsInvalidException_WhenRefreshingTokenIsInvalid() {

        doReturn(false).when(tokenProvider).validateToken(refreshToken);
        doReturn(username).when(tokenProvider).getUsernameFromToken(refreshToken);


        RuntimeException expectedException = assertThrows(RuntimeException.class, () -> {
            defaultJwtTokenManager.refreshToken(tokensDto);
        });


        assertEquals(RefreshingTokenIsInvalidException.class, expectedException.getClass());
        assertEquals(ExceptionConstantMessage.REFRESHING_TOKEN_IS_INVALID_EXCEPTION_MESSAGE, expectedException.getMessage());
        verify(tokenProvider, times(1)).validateToken(tokensDto.refreshToken());
        verify(tokenProvider, times(1)).getUsernameFromToken(tokensDto.refreshToken());
        verifyNoMoreInteractions(tokenProvider);
        verifyNoInteractions(userDetailsService);

        verify(tokenProvider).validateToken(tokenCaptor.capture());
        assertEquals(tokensDto.refreshToken(), tokenCaptor.getValue());

        verify(tokenProvider).getUsernameFromToken(tokenCaptor.capture());
        assertEquals(refreshToken, tokenCaptor.getValue());
    }


    @Test
    void getUserNotFoundException_WhenUsernameNotFound() {

        doReturn(true).when(tokenProvider).validateToken(refreshToken);
        doReturn(username).when(tokenProvider).getUsernameFromToken(refreshToken);
        doThrow(UsernameNotFoundException.class).when(userDetailsService).loadUserByUsername(username);

        RuntimeException expectedException = assertThrows(RuntimeException.class,
                () -> defaultJwtTokenManager.refreshToken(tokensDto));

        assertEquals(UsernameNotFoundException.class, expectedException.getClass());


        verify(tokenProvider, times(1)).validateToken(any());
        verify(tokenProvider, times(1)).getUsernameFromToken(any());
        verifyNoMoreInteractions(tokenProvider);

        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(userDetailsService);

        verify(tokenProvider).validateToken(tokenCaptor.capture());
        assertEquals(refreshToken, tokenCaptor.getValue());

        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue());

    }

    @Test
    @DisplayName("getNewAccessToken")
    void getNewAccessTokenAndOldRefreshToken_WhenRefreshTokenIsValid() {

        doReturn(true).when(tokenProvider).validateToken(refreshToken);
        doReturn(username).when(tokenProvider).getUsernameFromToken(refreshToken);
        doReturn(jwtPrincipal).when(userDetailsService).loadUserByUsername(username);
        doReturn(newAccessToken).when(tokenProvider).generateAccessToken(jwtUserPrincipal, accessTokenLifeTime);
        doReturn(false).when(tokenProvider)
                .isTokenExpiredSoon(tokensDto.refreshToken(), expirationDateLess24hInMs);


        TokensDto result = defaultJwtTokenManager.refreshToken(tokensDto);

        assertNotNull(result);
        assertEquals(refreshToken, result.refreshToken());
        assertEquals(newAccessToken, result.accessToken());

        verify(tokenProvider, times(1)).validateToken(refreshToken);
        verify(tokenProvider, times(1)).getUsernameFromToken(refreshToken);
        verify(tokenProvider, times(1)).generateAccessToken(jwtUserPrincipal, accessTokenLifeTime);
        verifyNoMoreInteractions(tokenProvider);

        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(userDetailsService);

        verify(tokenProvider).validateToken(tokenCaptor.capture());
        assertEquals(refreshToken, tokenCaptor.getValue());

        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue());
    }


    @Test
    @DisplayName("getNewAccessAndRefreshToken")
    void getNewAccessTokenAndNewRefreshToken_WhenTokenIsValid() {

        doReturn(true).when(tokenProvider).validateToken(refreshToken);
        doReturn(username).when(tokenProvider).getUsernameFromToken(refreshToken);
        doReturn(jwtPrincipal).when(userDetailsService).loadUserByUsername(username);
        doReturn(newAccessToken).when(tokenProvider).generateAccessToken(jwtUserPrincipal, accessTokenLifeTime);
        doReturn(true).when(tokenProvider)
                .isTokenExpiredSoon(tokensDto.refreshToken(), expirationDateLess24hInMs);
        doReturn(newRefreshToken).when(tokenProvider).generateRefreshToken(jwtUserPrincipal, refreshTokenLifeTime);


        TokensDto result = defaultJwtTokenManager.refreshToken(tokensDto);

        assertNotNull(result);
        assertEquals(newRefreshToken, result.refreshToken());
        assertEquals(newAccessToken, result.accessToken());

        verify(tokenProvider, times(1)).validateToken(refreshToken);
        verify(tokenProvider, times(1)).getUsernameFromToken(refreshToken);
        verify(tokenProvider, times(1)).generateAccessToken(jwtUserPrincipal, accessTokenLifeTime);
        verifyNoMoreInteractions(tokenProvider);

        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(userDetailsService);

        verify(tokenProvider).validateToken(tokenCaptor.capture());
        assertEquals(refreshToken, tokenCaptor.getValue());

        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue());
    }


}
