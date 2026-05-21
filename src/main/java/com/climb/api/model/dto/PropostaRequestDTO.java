package com.climb.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PropostaRequestDTO(
        @NotNull Long empresaId,
        @NotNull Long usuarioId,
        @NotBlank String status,
        String url,
        LocalDate dataCriacao
) {
}