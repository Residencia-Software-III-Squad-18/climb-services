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

    public Long getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Long idDocumento) { this.idDocumento = idDocumento; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getValidado() { return validado; }
    public void setValidado(String validado) { this.validado = validado; }

    public Usuario getAnalista() { return analista; }
    public void setAnalista(Usuario analista) { this.analista = analista; }
}