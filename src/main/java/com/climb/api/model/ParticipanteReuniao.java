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

}