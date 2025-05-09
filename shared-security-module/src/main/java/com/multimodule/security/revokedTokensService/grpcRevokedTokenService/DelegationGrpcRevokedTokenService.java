package com.multimodule.security.revokedTokensService.grpcRevokedTokenService;

public interface DelegationGrpcRevokedTokenService {

    boolean revokeTokenViaGrpc(String token);
    void isTokenRevokedViaGrpc(String token);

}
