package com.climb.api.model.dto;

import com.climb.api.model.enums.DocumentoStatus;

public record DocumentoResponseDTO(
        Long id,
        Long empresaId,
        String nomeEmpresa,
        String tipoDocumento,
        String url,
        DocumentoStatus validado,
        Long analistaId,
        String nomeAnalista
) {}