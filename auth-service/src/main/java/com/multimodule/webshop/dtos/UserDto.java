package com.multimodule.webshop.dtos;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;

public record UserDto(
        String username,
        Set<GrantedAuthority> authorities,
        boolean isAccountNonExpired,
        boolean isAccountNonLocked,
        boolean isCredentialsNonExpired,
        boolean isEnabled
) implements Serializable {
}
