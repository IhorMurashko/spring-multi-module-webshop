package com.multimodule.webshop.dtos;

public record TokensDto(
        String refreshToken,
        String accessToken

) {
}
