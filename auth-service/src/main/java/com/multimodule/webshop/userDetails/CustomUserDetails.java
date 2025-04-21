package com.multimodule.webshop.userDetails;


import com.multimodule.webshop.dtos.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public record CustomUserDetails(
        UserDto userDto


) implements UserDetails {

    public CustomUserDetails {
        if (userDto == null) {
            throw new UsernameNotFoundException("user can't be null");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.userDto.authorities();
    }

    @Override
    public String getPassword() {
        //todo: decide this issues;
        return null;
    }
    @Override
    public String getUsername() {
        return this.userDto.username();
    }
    @Override
    public boolean isAccountNonExpired() {
        return this.userDto.isAccountNonExpired();
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.userDto.isAccountNonLocked();
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.userDto.isCredentialsNonExpired();
    }
    @Override
    public boolean isEnabled() {
        return this.userDto.isEnabled();
    }
}
