package com.climb.api.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReuniaoDisponibilidadeResponseDTO {

    private Boolean disponivel;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer duracaoMinutos;
    private List<ConflitoDTO> conflitos = new ArrayList<>();

    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public List<ConflitoDTO> getConflitos() { return conflitos; }
    public void setConflitos(List<ConflitoDTO> conflitos) { this.conflitos = conflitos; }

    public static class ConflitoDTO {
        private String origem;
        private String tipo;
        private Long reuniaoId;
        private String googleEventId;
        private String titulo;
        private LocalTime horaInicio;
        private LocalTime horaFim;

        public String getOrigem() { return origem; }
        public void setOrigem(String origem) { this.origem = origem; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public Long getReuniaoId() { return reuniaoId; }
        public void setReuniaoId(Long reuniaoId) { this.reuniaoId = reuniaoId; }

        public String getGoogleEventId() { return googleEventId; }
        public void setGoogleEventId(String googleEventId) { this.googleEventId = googleEventId; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

        public LocalTime getHoraFim() { return horaFim; }
        public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    }
}
