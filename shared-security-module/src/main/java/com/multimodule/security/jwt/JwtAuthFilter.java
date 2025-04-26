package com.multimodule.security.jwt;


import com.multimodule.security.constants.SecurityConstants;
import com.multimodule.security.exceptions.RefreshingTokenIsInvalidException;
import com.multimodule.security.exceptions.RevokedTokenException;
import com.multimodule.security.revokedTokensService.RevokedTokenService;
import com.multimodule.security.utils.CustomExceptionsMessage;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenService revokedTokenService;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = extractToken(request);

            if (token != null && tokenProvider.validateToken(token)) {

                if (revokedTokenService.isTokenRevoked(token)) {
                    log.warn("Token is invalid or revoked: {}", token);
                    throw new RevokedTokenException(CustomExceptionsMessage.TOKEN_HAS_BEEN_REVOKED_EXCEPTION_MESSAGE);
                }

                String tokenType = tokenProvider.extractClaimFromToken(token, claims ->
                        claims.get(SecurityConstants.TOKEN_TYPE_CLAIM, String.class));

                if (SecurityConstants.REFRESH_TOKEN_TYPE.equals(tokenType)) {
                    log.error("Refresh token is not supported");
                    throw new RefreshingTokenIsInvalidException(CustomExceptionsMessage.REFRESHING_TOKEN_CANT_BE_USED_FOR_AUTHENTICATION_EXCEPTION_MESSAGE);
                }

                String userId = tokenProvider.extractClaimFromToken(token, claims ->
                        claims.get(SecurityConstants.USER_ID_CLAIM, String.class));
                String username = tokenProvider.getUsernameFromToken(token);
                List<String> roles = tokenProvider.getRolesFromToken(token);

                response.setHeader(SecurityConstants.HEADER_USER_ID, userId);
                response.setHeader(SecurityConstants.HEADER_USERNAME, username);
                response.setHeader(SecurityConstants.HEADER_ROLES, String.join(",", roles));

                log.info("Authenticated user {}, setting headers", username);
            }


        } catch (JwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            log.error("JwtAuthFilter: JwtException {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        try {
//            final String token = extractToken(request);
//
//            if (token != null && tokenProvider.validateToken(token)) {
//                if (revokedTokenService.isTokenRevoked(token)) {
//                    log.warn("Token is invalid or revoked: {}", token);
//                    throw new RevokedTokenException(CustomExceptionsMessage
//                            .TOKEN_HAS_BEEN_REVOKED_EXCEPTION_MESSAGE);
//                }
//                String tokenType = tokenProvider.extractClaimFromToken(token, claims ->
//                        claims.get(SecurityConstants.TOKEN_TYPE_CLAIM, String.class));
//                if (tokenType != null && tokenType.equals(SecurityConstants.REFRESH_TOKEN_TYPE)) {
//                    throw new RefreshingTokenIsInvalidException(CustomExceptionsMessage
//                            .REFRESHING_TOKEN_CANT_BE_USED_FOR_AUTHENTICATION_EXCEPTION_MESSAGE);
//                }
//
//                final String username = tokenProvider.getUsernameFromToken(token);
//                UserDetails user = userDetailsService.loadUserByUsername(username);
//
//                if (user != null) {
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    log.info("Set authentication in context holder for {}", user.getUsername());
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//        } catch (JwtException e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                    CustomExceptionsMessage.UNAUTHORIZED_EXCEPTION_MESSAGE);
//            log.error("JwtAuthFilter: JwtException {}", e.getMessage());
//            return;
//        }
//        filterChain.doFilter(request, response);
//    }


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
}
