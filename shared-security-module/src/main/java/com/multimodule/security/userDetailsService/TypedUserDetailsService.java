package com.multimodule.security.userDetailsService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface TypedUserDetailsService<T extends UserDetails> {
    T loadUserByUsername(String username) throws UsernameNotFoundException;
}
