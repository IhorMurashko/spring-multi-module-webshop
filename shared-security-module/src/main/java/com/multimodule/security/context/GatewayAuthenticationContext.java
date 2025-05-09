package com.multimodule.security.context;

import com.multimodule.security.constants.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequestScope
public class GatewayAuthenticationContext {

    private final HttpServletRequest request;

    public GatewayAuthenticationContext(HttpServletRequest request) {
        this.request = request;
    }

    public Optional<String> getUserId() {
        return Optional.ofNullable(request.getHeader(SecurityConstants.HEADER_USER_ID));
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(request.getHeader(SecurityConstants.HEADER_USERNAME));
    }

    public Optional<String> getTokenId() {
        return Optional.ofNullable(request.getHeader(SecurityConstants.HEADER_TOKEN_ID));
    }

    public List<String> getRoles() {
        String rolesHeader = request.getHeader(SecurityConstants.HEADER_ROLES);
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return List.of();
        }
        return Arrays.asList(rolesHeader.split(","));
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public boolean isAuthenticated() {
        return getUserId().isPresent();
    }
}
