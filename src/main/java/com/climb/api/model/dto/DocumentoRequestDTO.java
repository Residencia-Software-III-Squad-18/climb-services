package com.climb.api.model.dto;

public record DocumentoRequestDTO(
        Long empresaId,
        String tipoDocumento
) {
}
