package com.multimodule.security.userDetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtPrincipal extends UserDetails {

    Long getUserId();
}
