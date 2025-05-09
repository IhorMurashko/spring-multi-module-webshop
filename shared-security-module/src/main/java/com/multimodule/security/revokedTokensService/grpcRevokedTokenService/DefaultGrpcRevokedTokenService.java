package com.multimodule.security.revokedTokensService.grpcRevokedTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DefaultGrpcRevokedTokenService implements BasicGrpcRevokedTokenService {

    private final DelegationGrpcRevokedTokenService delegationTokenRevokedService;

    @Override
    public boolean isTokenRevoked(String token) {
        return delegationTokenRevokedService.revokeTokenViaGrpc(token);
    }

    @Override
    public void revokeToken(String token) {
        delegationTokenRevokedService.isTokenRevokedViaGrpc(token);
    }
}
