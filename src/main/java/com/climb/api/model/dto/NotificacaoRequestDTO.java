package com.climb.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NotificacaoRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotBlank
    @Size(max = 255)
    private String mensagem;

    @NotBlank
    @Size(max = 100)
    private String tipo;

    private LocalDate dataEnvio;
}