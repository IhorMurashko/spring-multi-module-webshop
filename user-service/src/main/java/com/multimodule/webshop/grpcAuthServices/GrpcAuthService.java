package com.multimodule.webshop.grpcAuthServices;

import auth.AuthServiceGrpc;
import auth.Result;
import com.multimodule.webshop.authUserServices.AuthUserService;
import com.multimodule.webshop.dto.UserAuthCredentials;
import com.multimodule.webshop.mapper.GrpcUserMapper;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.proto.common.AuthCredentials;
import com.multimodule.webshop.proto.common.User;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class GrpcAuthService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthUserService authUserService;
    private final GrpcUserMapper grpcUserMapper;

    @Override
    public void registration(AuthCredentials request, StreamObserver<Result> responseObserver) {

        try {
            boolean registered = authUserService.registration(new UserAuthCredentials(
                    request.getUsername(),
                    request.getPassword()
            ));

            responseObserver.onNext(Result.newBuilder()
                    .setResult(registered)
                    .build());
            responseObserver.onCompleted();

        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Invalid username or password")
                    .withCause(ex)
                    .asRuntimeException());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }


    @Override
    public void authentication(AuthCredentials request, StreamObserver<User> responseObserver) {

        try {
            Consumer authUser = authUserService.authentication(new UserAuthCredentials(
                    request.getUsername(),
                    request.getPassword()
            ));


            responseObserver.onNext(
                    grpcUserMapper.toDto(authUser)
            );
            responseObserver.onCompleted();
        } catch (RuntimeException ex) {

            responseObserver.onError(Status.CANCELLED
                    .withDescription("Invalid username or password")
                    .withCause(ex)
                    .asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(ex)
                    .asRuntimeException());
        }


    }
}
