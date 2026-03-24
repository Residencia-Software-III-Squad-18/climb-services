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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }
}