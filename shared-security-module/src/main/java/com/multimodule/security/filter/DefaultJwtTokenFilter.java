package com.multimodule.security.filter;

import com.multimodule.security.constants.SecurityConstants;
import com.multimodule.security.exceptions.ExceptionsConstantsMessage;
import com.multimodule.security.exceptions.tokenExceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.exceptions.tokenExceptions.RevokedTokenException;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.revokedTokensService.BasicRevokedTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default filter for processing JWT access tokens.
 * <p>
 * This filter:
 * <ul>
 *     <li>Extracts token from Authorization header</li>
 *     <li>Validates the token</li>
 *     <li>Checks whether it has been revoked</li>
 *     <li>Rejects refresh tokens used for access</li>
 *     <li>Injects extracted user information into the request attributes</li>
 * </ul>
 * <p>
 * The filter does not set Spring Security context directly. Instead, it passes the
 * user information for downstream filters or logic (e.g. Gateway header injection).
 */

@RequiredArgsConstructor
@Slf4j
public class DefaultJwtTokenFilter extends BasicOnePerRequestFilter {

    private final BasicJwtTokenProvider tokenProvider;
    private final BasicRevokedTokenService basicRevokedTokenService;

    /**
     * Performs filtering of incoming HTTP request and validates JWT token (if present).
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if the filter fails
     * @throws IOException      if I/O errors occur
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = extractToken(request);

            if (token != null && tokenProvider.validateToken(token)) {

                if (basicRevokedTokenService.isTokenRevoked(token)) {
                    log.warn("Token is invalid or revoked: {}", token);
                    throw new RevokedTokenException(ExceptionsConstantsMessage.TOKEN_HAS_BEEN_REVOKED_EXCEPTION_MESSAGE);
                }

                Map<String, Object> userClaims = tokenProvider.extractClaimFromToken(token, claims ->
                        claims.entrySet()
                                .stream()
                                .filter(e -> e.getKey() != null)
                                .collect(Collectors.toUnmodifiableMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                ))
                );

                if (userClaims.get(SecurityConstants.TOKEN_TYPE_CLAIM).equals(SecurityConstants.REFRESH_TOKEN_TYPE)) {
                    log.error("Refresh token is not supported");
                    throw new RefreshingTokenIsInvalidException(ExceptionsConstantsMessage
                            .REFRESHING_TOKEN_CANT_BE_USED_FOR_AUTHENTICATION_EXCEPTION_MESSAGE);
                }

                injectClaimsToRequest(request, userClaims);

                log.info("Authenticated user {}", userClaims.getOrDefault(SecurityConstants.USERNAME_CLAIM,
                        "username isn't exist in the request"));
            }

        } catch (JwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            log.error("JwtAuthFilter: JwtException {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts Bearer token from the request's Authorization header.
     *
     * @param request the HTTP request
     * @return token string or null if not found or invalid
     */
    private String extractToken(HttpServletRequest request) {
        final String requestToken = request.getHeader(SecurityConstants.AUTHENTICATION_HEADER);

        if (requestToken == null) {
            log.warn("No token in request to {} {}", request.getMethod(), request.getRequestURI());
            return null;
        }
        if (!requestToken.startsWith(SecurityConstants.BEARER_PREFIX)) {
            log.warn("Token does not begin with Bearer");
            return null;
        }
        return requestToken.substring(7);
    }

    /**
     * Injects extracted JWT claims into the current HTTP request attributes.
     * These attributes can be used by downstream filters or services.
     *
     * @param request the HTTP request
     * @param claims  map of claims to inject
     */
    private void injectClaimsToRequest(@NonNull HttpServletRequest request,
                                       @NonNull Map<String, Object> claims) {
        claims.forEach(request::setAttribute);
    }
}