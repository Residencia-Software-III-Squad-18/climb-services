package com.climb.api.model;

import com.climb.api.model.enums.ContratoStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Long idContrato;

    @OneToOne
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposta proposta;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContratoStatus status;

    public Long getIdContrato() { return idContrato; }
    public void setIdContrato(Long idContrato) { this.idContrato = idContrato; }

    public Proposta getProposta() { return proposta; }
    public void setProposta(Proposta proposta) { this.proposta = proposta; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public ContratoStatus getStatus() { return status; }
    public void setStatus(ContratoStatus status) { this.status = status; }
}
