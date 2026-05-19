package com.climb.api.service;

import com.climb.api.model.Contrato;
import com.climb.api.model.Documento;
import com.climb.api.model.Empresa;
import com.climb.api.model.Notificacao;
import com.climb.api.model.Proposta;
import com.climb.api.model.Usuario;
import com.climb.api.model.enums.DocumentoStatus;
import com.climb.api.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class ContratoNotificacaoService {

    private static final String TIPO_CONTRATO = "CONTRATO";
    private static final String TIPO_CONTRATO_VENCIMENTO = "CONTRATO_VENCIMENTO";
    private static final String TIPO_PROPOSTA = "PROPOSTA";
    private static final String TIPO_DOCUMENTO = "DOCUMENTO";

    private final NotificacaoRepository notificacaoRepository;
    private final EmailService emailService;
    private final int diasAvisoVencimento;

    public ContratoNotificacaoService(NotificacaoRepository notificacaoRepository,
                                      EmailService emailService,
                                      @Value("${app.contract-notifications.expiration-warning-days:30}") int diasAvisoVencimento) {
        this.notificacaoRepository = notificacaoRepository;
        this.emailService = emailService;
        this.diasAvisoVencimento = diasAvisoVencimento;
    }

    public void notificarContratoAprovadoOuReprovado(Contrato anterior, Contrato atualizado) {
        if (!mudouParaAprovadoOuReprovado(anterior.getStatus(), atualizado.getStatus())) {
            return;
        }

        String status = statusNormalizado(atualizado.getStatus());
        String assunto = "Contrato " + status.toLowerCase();
        String mensagem = "Contrato #" + atualizado.getIdContrato() + " foi " + status.toLowerCase() + ".";
        notificarInteressadosContrato(atualizado, assunto, mensagem, TIPO_CONTRATO);
    }

    public void notificarPropostaAprovadaOuReprovada(Proposta anterior, Proposta atualizada) {
        if (!mudouParaAprovadoOuReprovado(anterior.getStatus(), atualizada.getStatus())) {
            return;
        }

        String status = statusNormalizado(atualizada.getStatus());
        String assunto = "Proposta " + status.toLowerCase();
        String mensagem = "Proposta #" + atualizada.getIdProposta() + " foi " + status.toLowerCase() + ".";
        notificarInteressadosProposta(atualizada, assunto, mensagem, TIPO_PROPOSTA);
    }

    public void notificarDocumentoAprovadoOuReprovado(DocumentoStatus anterior, Documento documento) {
        DocumentoStatus atual = documento.getValidado();
        if (Objects.equals(anterior, atual) || !statusDocumentoFinal(atual)) {
            return;
        }

        String status = atual.name().toLowerCase();
        String assunto = "Documento " + status;
        String mensagem = "Documento #" + documento.getIdDocumento()
                + " (" + valorOuNaoInformado(documento.getTipoDocumento()) + ") foi " + status + ".";
        notificarInteressadosDocumento(documento, assunto, mensagem, TIPO_DOCUMENTO);
    }

    public void notificarVencimentoProximo(Contrato contrato, LocalDate hoje) {
        if (contrato.getDataFim() == null || contrato.getDataFim().isBefore(hoje)) {
            return;
        }

        long diasRestantes = ChronoUnit.DAYS.between(hoje, contrato.getDataFim());
        if (diasRestantes > diasAvisoVencimento) {
            return;
        }

        String assunto = "Contrato com vencimento proximo";
        String mensagem = "Contrato #" + contrato.getIdContrato()
                + " vence em " + diasRestantes
                + " dia(s), na data " + contrato.getDataFim() + ".";

        notificarInteressadosContrato(contrato, assunto, mensagem, TIPO_CONTRATO_VENCIMENTO);
    }

    private void notificarInteressadosContrato(Contrato contrato, String assunto, String mensagem, String tipo) {
        Usuario responsavel = obterResponsavel(contrato);
        Empresa empresa = obterEmpresa(contrato);

        if (responsavel != null) {
            salvarNotificacao(responsavel, mensagem, tipo);
            emailService.enviarEmail(responsavel.getEmail(), assunto, montarCorpo(responsavel.getNomeCompleto(), mensagem));
        }

        if (empresa != null && empresa.getEmail() != null && !empresa.getEmail().isBlank()) {
            emailService.enviarEmail(empresa.getEmail(), assunto, montarCorpo(empresa.getNomeFantasia(), mensagem));
        }
    }

    private void notificarInteressadosProposta(Proposta proposta, String assunto, String mensagem, String tipo) {
        Usuario responsavel = proposta.getUsuario();
        Empresa empresa = proposta.getEmpresa();

        if (responsavel != null) {
            salvarNotificacao(responsavel, mensagem, tipo);
            emailService.enviarEmail(responsavel.getEmail(), assunto, montarCorpo(responsavel.getNomeCompleto(), mensagem));
        }

        if (empresa != null && empresa.getEmail() != null && !empresa.getEmail().isBlank()) {
            emailService.enviarEmail(empresa.getEmail(), assunto, montarCorpo(empresa.getNomeFantasia(), mensagem));
        }
    }

    private void notificarInteressadosDocumento(Documento documento, String assunto, String mensagem, String tipo) {
        Usuario analista = documento.getAnalista();
        Empresa empresa = documento.getEmpresa();

        if (analista != null) {
            salvarNotificacao(analista, mensagem, tipo);
            emailService.enviarEmail(analista.getEmail(), assunto, montarCorpo(analista.getNomeCompleto(), mensagem));
        }

        if (empresa != null && empresa.getEmail() != null && !empresa.getEmail().isBlank()) {
            emailService.enviarEmail(empresa.getEmail(), assunto, montarCorpo(empresa.getNomeFantasia(), mensagem));
        }
    }

    private void salvarNotificacao(Usuario usuario, String mensagem, String tipo) {
        LocalDate hoje = LocalDate.now();
        boolean jaRegistradaHoje = notificacaoRepository
                .existsByUsuario_IdAndMensagemAndTipoAndDataEnvio(usuario.getId(), mensagem, tipo, hoje);

        if (jaRegistradaHoje) {
            return;
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(hoje);
        notificacaoRepository.save(notificacao);
    }

    private Usuario obterResponsavel(Contrato contrato) {
        Proposta proposta = contrato.getProposta();
        return proposta != null ? proposta.getUsuario() : null;
    }

    private Empresa obterEmpresa(Contrato contrato) {
        Proposta proposta = contrato.getProposta();
        return proposta != null ? proposta.getEmpresa() : null;
    }

    private String montarCorpo(String nomeDestino, String mensagem) {
        String saudacao = nomeDestino == null || nomeDestino.isBlank() ? "Ola" : "Ola, " + nomeDestino;
        return saudacao + "!\n\n" + mensagem + "\n\nAtenciosamente,\nEquipe Climb";
    }

    private String valorOuNaoInformado(Object valor) {
        return valor != null ? valor.toString() : "nao informado";
    }

    private boolean mudouParaAprovadoOuReprovado(String anterior, String atual) {
        String anteriorNormalizado = statusNormalizado(anterior);
        String atualNormalizado = statusNormalizado(atual);
        return !Objects.equals(anteriorNormalizado, atualNormalizado)
                && ("APROVADO".equals(atualNormalizado) || "REPROVADO".equals(atualNormalizado));
    }

    private String statusNormalizado(String status) {
        if (status == null) {
            return "";
        }

        String normalizado = status.trim().toUpperCase();
        if (normalizado.endsWith("A")) {
            normalizado = normalizado.substring(0, normalizado.length() - 1) + "O";
        }
        return normalizado;
    }

    private boolean statusDocumentoFinal(DocumentoStatus status) {
        return status == DocumentoStatus.APROVADO || status == DocumentoStatus.REPROVADO;
    }
}
