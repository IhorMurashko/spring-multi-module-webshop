package com.multimodule.security.jwt.tokenManagement.provider;

import com.multimodule.security.constants.SecurityConstants;
import com.multimodule.security.exceptions.ExceptionConstantMessage;
import com.multimodule.security.exceptions.tokenExceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.utils.DateObserver;
import com.multimodule.security.userDetails.JwtPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class DefaultJwtTokenProvider implements BasicJwtTokenProvider {
    //todo:  generate new stateful secret key;
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    @Override
    public String generateRefreshToken(JwtPrincipal userDetails, long validityPeriodInSeconds) {

        Date now = new Date();
        Date expiryDate = DateObserver.dateExpirationGenerator(now, validityPeriodInSeconds);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim(SecurityConstants.USER_ID_CLAIM, userDetails.getUserId())
                .claim(SecurityConstants.USERNAME_CLAIM, userDetails.getUsername())
                .claim(SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.REFRESH_TOKEN_TYPE)
                .signWith(key)
                .compact();
    }

    @Override
    public String generateAccessToken(JwtPrincipal userDetails, long validityPeriodInSeconds) {

        Date now = new Date();
        Date expirationDate = DateObserver.dateExpirationGenerator(now, validityPeriodInSeconds);


        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .claim(SecurityConstants.USER_ID_CLAIM, userDetails.getUserId())
                .claim(SecurityConstants.USERNAME_CLAIM, userDetails.getUsername())
                .claim(SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.ACCESS_TOKEN_TYPE)
                .claim(SecurityConstants.ROLES_CLAIM, roles)
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT claim: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public List<String> getRolesFromToken(String token) {
        return extractClaimFromToken(token, claims -> {
            Object rolesObject = claims.get(SecurityConstants.ROLES_CLAIM);
            if (rolesObject instanceof List<?>) {
                return ((List<?>) rolesObject).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
            return Collections.emptyList();
        });
    }

    @Override
    public boolean isTokenExpiredSoon(String refreshToken, long expectedExpirationTimeMs) {
        if (!SecurityConstants.REFRESH_TOKEN_TYPE.equals(refreshToken)) {
            throw new RefreshingTokenIsInvalidException(
                    ExceptionConstantMessage
                            .ACCESS_TOKEN_CANT_BE_USED_FOR_GETTING_NEW_REFRESH_TOKEN_EXCEPTION_MESSAGE
            );
        }

        Date expirationDate = extractClaimFromToken(refreshToken, Claims::getExpiration);
        if (expirationDate == null) {
            return true;
        }
        long remainingMillis = expirationDate.getTime() - System.currentTimeMillis();

        return remainingMillis < expectedExpirationTimeMs;
    }

    @Override
    public <T> T extractClaimFromToken(String token, Function<Claims, T> function) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
            return function.apply(claims);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token {}", ex.getMessage());
            throw ex;
        }
    }


}
