package com.climb.api.model.dto;

public record DocumentoResponseDTO(
        Long id,
        Long empresaId,
        String nomeEmpresa,
        String tipoDocumento,
        String url,
        String validado,
        Long analistaId,
        String nomeAnalista
) {}