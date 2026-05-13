package com.climb.api.model.dto;

import java.time.LocalDate;

public record PropostaResponseDTO(
        Long idProposta,
        Long empresaId,
        Long usuarioId,
        String status,
        String url,
        LocalDate dataCriacao
) {
}