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

    public Long getIdPermissao() { return idPermissao; }
    public void setIdPermissao(Long idPermissao) { this.idPermissao = idPermissao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}