package com.climb.api.model.dto;

import lombok.Data;

@Data
public class CompleteGoogleRegistrationRequestDTO {
    private String pendingToken;
    private String cpf;
    private String contato;
    private String senha;
    private Long cargoId;
}
