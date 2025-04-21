package com.multimodule.webshop.grpcAuthServices;

import com.multimodule.webshop.dtos.AuthCredentialsDto;
import com.multimodule.webshop.dtos.TokensDto;
import com.multimodule.webshop.proto.common.AuthCredentials;

public interface GrpcAuthService {

    boolean registration(AuthCredentialsDto credentials);

    TokensDto authenticate(AuthCredentialsDto credentials);


}
