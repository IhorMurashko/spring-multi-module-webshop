package com.multimodule.security.jwt.tokenManagement.provider;

import com.multimodule.security.userDetails.JwtPrincipal;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.function.Function;

public interface BasicJwtTokenProvider {

    String generateRefreshToken(JwtPrincipal userDetails, long validityPeriodInSeconds);

    String generateAccessToken(JwtPrincipal userDetails, long validityPeriodInSeconds);

    boolean validateToken(String token);

    <T> T extractClaimFromToken(String token, Function<Claims, T> function);
    String getUsernameFromToken(String token);

    List<String> getRolesFromToken(String token);

    boolean isTokenExpiredSoon(String refreshToken, long expectedExpirationTimeMs);

}
