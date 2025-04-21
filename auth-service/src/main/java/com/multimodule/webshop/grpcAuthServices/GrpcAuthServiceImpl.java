package com.multimodule.webshop.grpcAuthServices;

import auth.AuthServiceGrpc;
import auth.Result;
import com.multimodule.webshop.dtos.AuthCredentialsDto;
import com.multimodule.webshop.dtos.TokensDto;
import com.multimodule.webshop.dtos.UserDto;
import com.multimodule.webshop.jwt.JwtTokenProvider;
import com.multimodule.webshop.mapper.UserMapper;
import com.multimodule.webshop.proto.common.AuthCredentials;
import com.multimodule.webshop.proto.common.User;
import com.multimodule.webshop.userDetails.CustomUserDetails;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrpcAuthServiceImpl implements GrpcAuthService {

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public boolean registration(AuthCredentialsDto credentials) {
        try {
            Result registration = authServiceBlockingStub.registration(AuthCredentials.newBuilder()
                    .setUsername(credentials.username())
                    .setPassword(credentials.password())
                    .build());

            return registration.getResult();
        } catch (StatusRuntimeException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public TokensDto authenticate(AuthCredentialsDto credentials) {
        try {
            User authentication = authServiceBlockingStub.authentication(AuthCredentials.newBuilder()
                    .setUsername(credentials.username())
                    .setPassword(credentials.password())
                    .build());

            UserDto userDto = userMapper.toDto(authentication);

            UserDetails userDetails = new CustomUserDetails(userDto);

            final String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
            final String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            return new TokensDto(accessToken, refreshToken);

        } catch (StatusRuntimeException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }
}
