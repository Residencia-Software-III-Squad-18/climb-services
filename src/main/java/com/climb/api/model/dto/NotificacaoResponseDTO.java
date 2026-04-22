package com.climb.api.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NotificacaoResponseDTO {

    private Long idNotificacao;

    private Long usuarioId;

    private String mensagem;

    private LocalDate dataEnvio;

    private String tipo;
}