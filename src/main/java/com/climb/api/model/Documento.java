package com.climb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocumento;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "url")
    private String url;

    @Column(name = "validado")
    private String validado;

    @ManyToOne
    @JoinColumn(name = "analista_id")
    private Usuario analista;

}