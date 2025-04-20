package com.multimodule.webshop.controllers;

import com.multimodule.webshop.auth.ConsumerAuthService;
import com.multimodule.webshop.dto.AuthCredentials;
import com.multimodule.webshop.dto.TokensDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthCustomerController {

    private final ConsumerAuthService consumerAuthService;


    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> consumerRegistration(@Valid @RequestBody AuthCredentials credentials) {

        consumerAuthService.registration(credentials);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/auth")
    public ResponseEntity<TokensDto> auth(@Valid @RequestBody AuthCredentials credentials) {
        TokensDto tokensDto = consumerAuthService.authenticate(credentials);

        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }


}
