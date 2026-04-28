package com.climb.api.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ExchangeCodeRequestDTO(
    @NotBlank(message = "Code é obrigatório")
    String code
) {}
