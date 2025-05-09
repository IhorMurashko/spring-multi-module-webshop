package com.multimodule.security.revokedTokensService.grpcRevokedTokenService;

public interface BasicGrpcRevokedTokenService {

    boolean isTokenRevoked(String token);

    void revokeToken(String token);


}
