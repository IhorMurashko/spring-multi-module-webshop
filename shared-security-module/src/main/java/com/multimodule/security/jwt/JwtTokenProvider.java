package com.multimodule.security.jwt;


import com.multimodule.security.constants.SecurityConstants;
import com.multimodule.security.userDetails.JwtPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenProvider {
    //todo:  generate new stateful secret key;
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateRefreshToken(JwtPrincipal userDetails) {
        Date now = new Date();
        // Refresh token validity period: 30 days
        long refreshTokenExpirationMs = 2592000000L;
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim(SecurityConstants.USER_ID_CLAIM, userDetails.getUserId())
                .claim(SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.REFRESH_TOKEN_TYPE)
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(JwtPrincipal userDetails) {
        Date now = new Date();
        // Access token validity period: 15 minutes
        long accessTokenExpirationMs = 900_000;
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationMs);
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .claim(SecurityConstants.USER_ID_CLAIM, userDetails.getUserId())
                .claim(SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.ACCESS_TOKEN_TYPE)
                .claim(SecurityConstants.ROLES_CLAIM, roles)
                .signWith(key)
                .compact();
    }

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

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

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


    public boolean isRefreshTokenExpiredSoon(String refreshToken) {
        Date expirationDate = extractClaimFromToken(refreshToken, Claims::getExpiration);
        if (expirationDate == null) {
            return true;
        }
        long remainingMillis = expirationDate.getTime() - System.currentTimeMillis();
        long oneDayInMillis = 86400000L; // 24 hours
        return remainingMillis < oneDayInMillis;
    }

    public <T> T extractClaimFromToken(String token, Function<Claims, T> function) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token", ex);
        }
        return function.apply(claims);
    }
}
