package com.multimodule.webshop.grpcConfig;

import auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user_details.UserDetailsServiceGrpc;

@Configuration
public class GrpcClientConfig {

    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9091).
                usePlaintext().
                build();
        return AuthServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public UserDetailsServiceGrpc.UserDetailsServiceBlockingStub userDetailsServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9091).
                usePlaintext().
                build();
        return UserDetailsServiceGrpc.newBlockingStub(channel);
    }

}
