package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Long idServico;

    @Column(name = "nome")
    private String nome;

    public Long getIdServico() { return idServico; }
    public void setIdServico(Long idServico) { this.idServico = idServico; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}