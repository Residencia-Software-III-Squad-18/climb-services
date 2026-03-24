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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Permissao getPermissao() { return permissao; }
    public void setPermissao(Permissao permissao) { this.permissao = permissao; }
}