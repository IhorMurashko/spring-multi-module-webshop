package com.multimodule.webshop.grpcAuthServices;

import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.webshop.dtos.AuthCredentialsDto;

public interface GrpcAuthService {

    boolean registration(AuthCredentialsDto credentials);

    TokensDto authenticate(AuthCredentialsDto credentials);


}
