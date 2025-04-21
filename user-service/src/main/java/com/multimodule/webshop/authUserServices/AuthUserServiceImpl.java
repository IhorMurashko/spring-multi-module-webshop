package com.multimodule.webshop.authUserServices;

import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.userServices.ConsumerService;
import com.multimodule.webshop.dto.UserAuthCredentials;
import com.multimodule.webshop.exceptions.EmailAlreadyExistException;
import com.multimodule.webshop.model.AbstractUserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthUserServiceImpl implements AuthUserService {

    private final ConsumerService consumerService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean registration( UserAuthCredentials credentials) {

        if (consumerService.existsByUsername(credentials.username())) {
            throw new EmailAlreadyExistException("Email already exist");
        }

        consumerService.save(new Consumer(
                credentials.username().toLowerCase(),
                passwordEncoder.encode(credentials.password())
        ));

        return true;
    }

    @Override
    public Consumer authentication( UserAuthCredentials credentials) {

        Consumer byUsername = consumerService.findByUsername(credentials.username().toLowerCase());

        if (!passwordEncoder.matches(credentials.password(), byUsername.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return byUsername;
    }
}
