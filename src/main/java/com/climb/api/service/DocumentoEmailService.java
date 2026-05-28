package com.climb.api.service;

import com.climb.api.model.Documento;
import com.climb.api.model.Empresa;
import com.climb.api.model.Notificacao;
import com.climb.api.model.Usuario;
import com.climb.api.model.enums.DocumentoStatus;
import com.climb.api.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class DocumentoEmailService {

    private static final String TIPO_DOCUMENTO = "DOCUMENTO";

    private final EmailService emailService;
    private final NotificacaoRepository notificacaoRepository;

    public DocumentoEmailService(
            EmailService emailService,
            NotificacaoRepository notificacaoRepository
    ) {
        this.emailService = emailService;
        this.notificacaoRepository = notificacaoRepository;
    }

    public void enviarSolicitacaoDocumentacao(Documento documento) {

        String assunto = "Solicitação de documentação";

        String mensagem = "Uma nova documentação foi solicitada.\n\n"
                + "Documento: " + valorOuPadrao(
                documento.getTipoDocumento(),
                "Documento solicitado"
        ) + "\n"
                + "Empresa: " + obterNomeEmpresa(documento);

        notificarInteressados(documento, assunto, mensagem);
    }

    public void enviarResultadoValidacao(Documento documento) {

        if (documento.getValidado() == DocumentoStatus.APROVADO) {

            String assunto = "Documentação aprovada";

            String mensagem = "A documentação enviada foi aprovada.\n\n"
                    + "Documento: " + valorOuPadrao(
                    documento.getTipoDocumento(),
                    "Documento"
            ) + "\n"
                    + "Empresa: " + obterNomeEmpresa(documento);

            notificarInteressados(documento, assunto, mensagem);
        }

        if (documento.getValidado() == DocumentoStatus.REPROVADO) {

            String assunto = "Documentação não conforme";

            String mensagem = "A documentação enviada não está conforme.\n\n"
                    + "Documento: " + valorOuPadrao(
                    documento.getTipoDocumento(),
                    "Documento"
            ) + "\n"
                    + "Empresa: " + obterNomeEmpresa(documento)
                    + "\n\nPor favor, envie uma nova versão.";

            notificarInteressados(documento, assunto, mensagem);
        }
    }

    private void notificarInteressados(
            Documento documento,
            String assunto,
            String mensagem
    ) {

        Usuario analista = documento.getAnalista();
        Empresa empresa = documento.getEmpresa();

        if (analista != null && StringUtils.hasText(analista.getEmail())) {

            salvarNotificacao(
                    analista,
                    mensagem,
                    TIPO_DOCUMENTO
            );

            emailService.enviarEmail(
                    analista.getEmail(),
                    assunto,
                    montarCorpo(
                            analista.getNomeCompleto(),
                            mensagem
                    )
            );
        }

        if (empresa != null && StringUtils.hasText(empresa.getEmail())) {

            emailService.enviarEmail(
                    empresa.getEmail(),
                    assunto,
                    montarCorpo(
                            empresa.getNomeFantasia(),
                            mensagem
                    )
            );
        }
    }

    private void salvarNotificacao(
            Usuario usuario,
            String mensagem,
            String tipo
    ) {

        LocalDate hoje = LocalDate.now();

        boolean jaRegistradaHoje =
                notificacaoRepository
                        .existsByUsuario_IdAndMensagemAndTipoAndDataEnvio(
                                usuario.getId(),
                                mensagem,
                                tipo,
                                hoje
                        );

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

    private String obterNomeEmpresa(Documento documento) {

        if (documento.getEmpresa() == null) {
            return "Não informada";
        }

        return valorOuPadrao(
                documento.getEmpresa().getNomeFantasia(),
                documento.getEmpresa().getRazaoSocial()
        );
    }

    private String montarCorpo(
            String nomeDestino,
            String mensagem
    ) {

        String saudacao =
                StringUtils.hasText(nomeDestino)
                        ? "Olá, " + nomeDestino
                        : "Olá";

        return saudacao
                + "!\n\n"
                + mensagem
                + "\n\nAtenciosamente,\nEquipe Climb";
    }

    private String valorOuPadrao(
            String valor,
            String padrao
    ) {

        return StringUtils.hasText(valor)
                ? valor
                : padrao;
    }
}