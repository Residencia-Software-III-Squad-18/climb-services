package com.climb.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "relatorios")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relatorio")
    private Long idRelatorio;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Column(name = "url_pdf")
    private String urlPdf;

    @Column(name = "data_envio")
    private LocalDate dataEnvio;

}