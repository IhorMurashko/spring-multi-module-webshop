package com.multimodule.security.dtos;

public record TokensDto(
        String accessToken,
        String refreshToken
) {
}
