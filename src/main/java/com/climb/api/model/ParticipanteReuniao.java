package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "participantes_reuniao")
public class ParticipanteReuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_reuniao")
    private Reuniao reuniao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Reuniao getReuniao() { return reuniao; }
    public void setReuniao(Reuniao reuniao) { this.reuniao = reuniao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}