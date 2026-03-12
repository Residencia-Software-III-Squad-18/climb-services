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

}
