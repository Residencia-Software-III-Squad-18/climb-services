package com.climb.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

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

    public Long getIdReuniao() { return idReuniao; }
    public void setIdReuniao(Long idReuniao) { this.idReuniao = idReuniao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Boolean getPresencial() { return presencial; }
    public void setPresencial(Boolean presencial) { this.presencial = presencial; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getPauta() { return pauta; }
    public void setPauta(String pauta) { this.pauta = pauta; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}