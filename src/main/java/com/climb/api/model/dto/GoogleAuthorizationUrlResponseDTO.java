package com.climb.api.model.dto;

public record GoogleAuthorizationUrlResponseDTO(
        String authorizationUrl,
        String redirectUri,
        String scope
) {
}
