package com.climb.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleOAuthResolveResponseDTO {
    private String status;
    private LoginResponseDTO login;
    private String pendingToken;
    private String email;
    private String nome;
    private String avatarUrl;
    private String message;
}
