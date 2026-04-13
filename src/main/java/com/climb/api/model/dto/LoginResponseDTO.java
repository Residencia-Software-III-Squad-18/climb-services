package com.climb.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {
    @JsonProperty("accessToken")
    private String accessToken;
    
    @JsonProperty("refreshToken")
    private String refreshToken;
    
    @JsonProperty("usuario")
    private UsuarioResponseDTO usuario;
    
    @JsonProperty("expiresIn")
    private long expiresIn;

    public LoginResponseDTO(String accessToken, String refreshToken, UsuarioResponseDTO usuario, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.usuario = usuario;
        this.expiresIn = expiresIn;
    }
}
