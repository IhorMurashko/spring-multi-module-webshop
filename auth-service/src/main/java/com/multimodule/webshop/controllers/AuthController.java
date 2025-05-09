package com.multimodule.webshop.controllers;

import com.multimodule.security.dto.token.TokensDto;
import com.multimodule.webshop.dtos.AuthCredentialsDto;
import com.multimodule.webshop.grpcAuthServices.GrpcAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GrpcAuthService grpcAuthService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(
            @NonNull @RequestBody AuthCredentialsDto authCredentials) {

        grpcAuthService.registration(authCredentials);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<TokensDto> auth(@RequestBody AuthCredentialsDto authCredentials) {
        TokensDto tokensDto = grpcAuthService.authenticate(authCredentials);

        return ResponseEntity.ok(tokensDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public ResponseEntity<HttpStatus> logout() {
        //todo: dev logout
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
