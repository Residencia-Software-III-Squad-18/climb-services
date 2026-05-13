package com.climb.api.model.dto;

import com.climb.api.model.enums.DocumentoStatus;
import jakarta.validation.constraints.NotNull;

public record DocumentoValidacaoRequestDTO(
        @NotNull(message = "O status é obrigatório")
        DocumentoStatus validado
) {}
