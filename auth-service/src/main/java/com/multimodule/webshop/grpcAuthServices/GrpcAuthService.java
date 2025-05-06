package com.multimodule.webshop.grpcAuthServices;

import com.multimodule.security.dtos.token.TokensDto;
import com.multimodule.webshop.dtos.AuthCredentialsDto;

public interface GrpcAuthService {

    boolean registration(AuthCredentialsDto credentials);

    TokensDto authenticate(AuthCredentialsDto credentials);


}
