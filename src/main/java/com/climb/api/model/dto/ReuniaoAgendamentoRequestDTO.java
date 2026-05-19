package com.climb.api.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReuniaoAgendamentoRequestDTO {

    @NotBlank(message = "Titulo e obrigatorio")
    private String titulo;

    private String pauta;

    @NotNull(message = "Data e obrigatoria")
    private LocalDate data;

    @NotNull(message = "Horario e obrigatorio")
    private LocalTime hora;

    private Boolean presencial;
    private String local;

    @NotNull(message = "Empresa e obrigatoria")
    private Long empresaId;

    private String status;

    @Min(value = 1, message = "Duracao deve ser maior que zero")
    private Integer duracaoMinutos;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getPauta() { return pauta; }
    public void setPauta(String pauta) { this.pauta = pauta; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Boolean getPresencial() { return presencial; }
    public void setPresencial(Boolean presencial) { this.presencial = presencial; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}
