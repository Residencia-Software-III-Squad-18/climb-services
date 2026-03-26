package com.climb.api.model.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String contato;
    private String senha;
    private String situacao;
    private Long cargoId;
}
