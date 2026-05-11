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

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "permissao_id")
    private Long permissaoId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioId = usuario != null ? usuario.getId() : null;
    }

    public Permissao getPermissao() { return permissao; }
    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
        this.permissaoId = permissao != null ? permissao.getIdPermissao() : null;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getPermissaoId() { return permissaoId; }
    public void setPermissaoId(Long permissaoId) { this.permissaoId = permissaoId; }
}
