package com.climb.api.model.dto;

public class UsuarioPermissaoDTO {

    private Long usuarioId;
    private Long permissaoId;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getPermissaoId() { return permissaoId; }
    public void setPermissaoId(Long permissaoId) { this.permissaoId = permissaoId; }
}