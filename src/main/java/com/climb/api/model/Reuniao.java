package com.climb.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reunioes")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reuniao")
    private Long idReuniao;

    @Column(nullable = false)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "presencial")
    private Boolean presencial;

    @Column(name = "local")
    private String local;

    @Column(name = "pauta", columnDefinition = "TEXT")
    private String pauta;

    @Column(name = "status")
    private String status;

}