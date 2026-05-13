package com.climb.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentoSolicitacaoRequestDTO(
        @NotNull(message = "O ID da empresa é obrigatório")
        Long empresaId,

        @NotBlank(message = "O tipo do documento é obrigatório")
        String tipoDocumento,

        @NotNull(message = "O ID do analista é obrigatório")
        Long analistaId
) {}