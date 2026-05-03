package com.climb.api.model.dto;

public record ExchangeCodeResponseDTO(
    String accessToken,
    String refreshToken,
    Long expiresIn,
    String googleAccessToken,
    String googleRefreshToken,
    UsuarioResponseDTO usuario
) {}
