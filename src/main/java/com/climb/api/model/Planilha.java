package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "planilhas")
public class Planilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilha")
    private Long idPlanilha;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Column(name = "url_google_sheets")
    private String urlGoogleSheets;

    @Column(name = "bloqueada")
    private Boolean bloqueada;

    @Column(name = "permissao_visualizacao")
    private String permissaoVisualizacao;

    public Long getIdPlanilha() { return idPlanilha; }
    public void setIdPlanilha(Long idPlanilha) { this.idPlanilha = idPlanilha; }

    public Contrato getContrato() { return contrato; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }

    public String getUrlGoogleSheets() { return urlGoogleSheets; }
    public void setUrlGoogleSheets(String urlGoogleSheets) { this.urlGoogleSheets = urlGoogleSheets; }

    public Boolean getBloqueada() { return bloqueada; }
    public void setBloqueada(Boolean bloqueada) { this.bloqueada = bloqueada; }

    public String getPermissaoVisualizacao() { return permissaoVisualizacao; }
    public void setPermissaoVisualizacao(String permissaoVisualizacao) { this.permissaoVisualizacao = permissaoVisualizacao; }
}