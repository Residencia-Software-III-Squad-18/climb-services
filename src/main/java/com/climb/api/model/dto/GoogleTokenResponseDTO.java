package com.climb.api.model.dto;

public record GoogleTokenResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String scope,
        String appAccessToken,
        String appRefreshToken,
        Long appExpiresIn,
        UsuarioResponseDTO usuario
) {
}
