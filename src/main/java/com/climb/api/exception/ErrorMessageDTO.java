package com.climb.api.exception;

public record ErrorMessageDTO(
        String message,
        String field,
        String code
) {
}
