package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_permissoes")
public class UsuarioPermissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_permissao")
    private Permissao permissao;

}