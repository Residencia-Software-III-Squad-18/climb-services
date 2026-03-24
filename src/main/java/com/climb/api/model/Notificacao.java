package com.climb.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long idNotificacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name = "data_envio")
    private LocalDate dataEnvio;

    @Column(name = "tipo")
    private String tipo;

    public Long getIdNotificacao() { return idNotificacao; }
    public void setIdNotificacao(Long idNotificacao) { this.idNotificacao = idNotificacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDate getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDate dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}