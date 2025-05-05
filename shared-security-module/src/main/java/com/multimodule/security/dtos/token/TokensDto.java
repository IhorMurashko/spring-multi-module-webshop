package com.multimodule.security.dtos.token;

/**
 * Data Transfer Object (DTO) representing a pair of JWT tokens.
 * <p>
 * This object is typically used for transferring both access and refresh tokens
 * between client and server during authentication and token refreshing processes.
 * </p>
 *
 * @param accessToken  the short-lived JWT access token used for authorizing requests
 * @param refreshToken the longer-lived JWT refresh token used for obtaining new access tokens
 */
public record TokensDto(
        String accessToken,
        String refreshToken
) {
}
