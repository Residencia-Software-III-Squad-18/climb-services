package com.climb.api.model.dto;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha

) {}
