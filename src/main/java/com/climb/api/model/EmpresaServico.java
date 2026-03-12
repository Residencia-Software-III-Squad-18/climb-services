package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa_servico")
public class EmpresaServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    private Servico servico;

}