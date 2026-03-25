package com.climb.api.model.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String contato;
    private String senhaHash;
    private Boolean ativo;
    private String cargoNome;
}
