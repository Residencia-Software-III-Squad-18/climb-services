package com.climb.api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contato;

    @Column(nullable = false)
    private String senhaHash;

    @Transient
    private String senha;

    @Column(nullable = false)
    private String situacao;

    @ManyToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @ManyToMany
    @JoinTable(
        name = "usuario_permissoes",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoes = new HashSet<>();

}