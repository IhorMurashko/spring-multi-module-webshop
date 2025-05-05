package com.multimodule.security.dtos.user;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public record AuthenticatedUserDto(
        String userId,
        String username,
        Set<GrantedAuthority> roles

) {
}
