package com.multimodule.security.roles;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    ROLE_ADMIN, ROLE_USER, ROLES_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
