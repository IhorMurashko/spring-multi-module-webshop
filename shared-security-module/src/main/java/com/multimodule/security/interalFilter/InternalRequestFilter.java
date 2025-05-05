package com.multimodule.security.interalFilter;

import com.multimodule.security.context.GatewayAuthenticationContext;
import com.multimodule.security.dtos.user.AuthenticatedUserDto;
import com.multimodule.security.exceptions.convertObject.InvalidConvertRoleException;
import com.multimodule.security.exceptions.gateway.HeaderDoesntContainsCredentialsException;
import com.multimodule.security.roles.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InternalRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            GatewayAuthenticationContext gatewayAuthenticationContext = new GatewayAuthenticationContext(request);

            String userId = gatewayAuthenticationContext.getUserId().orElseThrow(
                    () -> new HeaderDoesntContainsCredentialsException("Request header 'userId' is missing")
            );

            String username = gatewayAuthenticationContext.getUsername().orElseThrow(
                    () -> new HeaderDoesntContainsCredentialsException("Request header 'username' is missing")
            );

            Set<GrantedAuthority> userRoles = gatewayAuthenticationContext.getRoles()
                    .stream()
                    .map(role -> {
                        try {
                            return Roles.valueOf(role);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid role '{}' received in header for user '{}'", role, username);
                            throw new InvalidConvertRoleException("Invalid role: " + role);
                        }
                    })
                    .collect(Collectors.toSet());

            if (userRoles.isEmpty()) {
                log.warn("User '{}' has no roles assigned", username);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    new AuthenticatedUserDto(userId, username, userRoles),
                    null,
                    userRoles
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
