package com.multimodule.webshop.jwt;

import com.multimodule.webshop.redisServices.RevokedTokenServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenServiceImpl revokedTokenServiceImpl;

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
                if (revokedTokenServiceImpl.isTokenRevoked(token)) {
                    throw new JwtException("Token has been revoked");
                }
                String tokenType = tokenProvider.extractClaimFromToken(token, claims ->
                        claims.get("token_type", String.class));
                if (tokenType != null && tokenType.equals("refreshToken")) {
                    throw new JwtException("Refresh token can't be used for authentication");
                }

                final String username = tokenProvider.getUsernameFromToken(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("Set authentication in context holder for {}", user.getUsername());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            log.error("JwtAuthFilter: JwtException {}", e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request) {
        final String requestToken = request.getHeader("Authorization");

        if (requestToken == null) {
            log.warn("There is no Authorization header");
            return null;
        }
        if (!requestToken.startsWith("Bearer ")) {
            log.warn("Token does not begin with Bearer");
            return null;
        }
        return requestToken.substring(7);
    }
}
