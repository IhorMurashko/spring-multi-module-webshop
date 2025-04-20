package com.multimodule.webshop.auth;

import com.multimodule.webshop.dto.AuthCredentials;
import com.multimodule.webshop.dto.TokensDto;

public interface ConsumerAuthService {

    void registration(AuthCredentials credentials);

    TokensDto authenticate(AuthCredentials credentials);

}
