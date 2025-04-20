package com.multimodule.webshop.dto;

public record TokensDto(
        String refreshToken,
        String accessToken

) {
}
