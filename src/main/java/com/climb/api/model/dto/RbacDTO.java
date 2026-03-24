package com.climb.api.model.dto;

import com.climb.api.model.PermissaoCodigo;

import java.util.Set;

public class RbacDTO {

    private Long usuarioId;
    private String nomeUsuario;
    private String nomeCargo;
    private Set<PermissaoCodigo> permissoesDoCargo;
    private Set<PermissaoCodigo> permissoesIndividuais;
    private Set<PermissaoCodigo> permissoesEfetivas;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getNomeCargo() { return nomeCargo; }
    public void setNomeCargo(String nomeCargo) { this.nomeCargo = nomeCargo; }

    public Set<PermissaoCodigo> getPermissoesDoCargo() { return permissoesDoCargo; }
    public void setPermissoesDoCargo(Set<PermissaoCodigo> permissoesDoCargo) { this.permissoesDoCargo = permissoesDoCargo; }

    public Set<PermissaoCodigo> getPermissoesIndividuais() { return permissoesIndividuais; }
    public void setPermissoesIndividuais(Set<PermissaoCodigo> permissoesIndividuais) { this.permissoesIndividuais = permissoesIndividuais; }

    public Set<PermissaoCodigo> getPermissoesEfetivas() { return permissoesEfetivas; }
    public void setPermissoesEfetivas(Set<PermissaoCodigo> permissoesEfetivas) { this.permissoesEfetivas = permissoesEfetivas; }
}