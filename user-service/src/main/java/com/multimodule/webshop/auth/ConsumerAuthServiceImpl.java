package com.multimodule.webshop.auth;

import com.multimodule.webshop.dto.AuthCredentials;
import com.multimodule.webshop.dto.TokensDto;
import com.multimodule.webshop.exceptions.EmailAlreadyExistException;
import com.multimodule.webshop.jwt.JwtTokenProvider;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.consumerServices.ConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerAuthServiceImpl implements ConsumerAuthService {

    private final ConsumerService consumerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public void registration(AuthCredentials credentials) {

        final String username = credentials.username().toLowerCase();
        boolean existsByUsername = consumerService.existsByUsername(username);

        if (existsByUsername) {
            throw new EmailAlreadyExistException("Email already exists");
        }


        consumerService.save(new Consumer(username.toLowerCase(),
                passwordEncoder.encode(credentials.password())));

        log.info("Consumer registration successful with email: {}", username);
    }

    @Override
    public TokensDto authenticate(AuthCredentials credentials) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.username());
        final String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new TokensDto(accessToken, refreshToken);
    }
}
