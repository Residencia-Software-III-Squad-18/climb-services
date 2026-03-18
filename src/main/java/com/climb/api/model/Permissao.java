package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "permissoes")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permissao")
    private Long idPermissao;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "codigo", unique = true, nullable = false)
    private String codigo;

}