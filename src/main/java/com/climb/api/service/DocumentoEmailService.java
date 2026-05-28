package com.climb.api.service;

import com.climb.api.model.Documento;
import com.climb.api.model.enums.DocumentoStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DocumentoEmailService {

    private final EmailService emailService;

    public DocumentoEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void enviarSolicitacaoDocumentacao(Documento documento) {
        String emailDestino = obterEmailEmpresa(documento);

        String assunto = "Solicitação de documentação - Climb";

        String corpo = "Olá!\n\n"
                + "Uma nova documentação foi solicitada para sua empresa.\n\n"
                + "Documento: " + valorOuPadrao(documento.getTipoDocumento(), "Documento solicitado") + "\n"
                + "Empresa: " + obterNomeEmpresa(documento) + "\n\n"
                + "Por favor, envie o documento solicitado pelo sistema Climb.";

        emailService.enviarEmail(emailDestino, assunto, corpo);
    }

    public void enviarResultadoValidacao(Documento documento) {
        if (documento.getValidado() == DocumentoStatus.APROVADO) {
            enviarDocumentacaoConforme(documento);
        }

        if (documento.getValidado() == DocumentoStatus.REPROVADO) {
            enviarDocumentacaoNaoConforme(documento);
        }
    }

    private void enviarDocumentacaoConforme(Documento documento) {
        String assunto = "Documentação aprovada - Climb";

        String corpo = "Olá!\n\n"
                + "A documentação enviada foi analisada e está conforme.\n\n"
                + "Documento: " + valorOuPadrao(documento.getTipoDocumento(), "Documento") + "\n"
                + "Empresa: " + obterNomeEmpresa(documento) + "\n\n"
                + "Nenhuma ação adicional é necessária neste momento.";

        emailService.enviarEmail(obterEmailEmpresa(documento), assunto, corpo);
    }

    private void enviarDocumentacaoNaoConforme(Documento documento) {
        String assunto = "Documentação não conforme - Climb";

        String corpo = "Olá!\n\n"
                + "A documentação enviada foi analisada e não está conforme.\n\n"
                + "Documento: " + valorOuPadrao(documento.getTipoDocumento(), "Documento") + "\n"
                + "Empresa: " + obterNomeEmpresa(documento) + "\n\n"
                + "Por favor, revise o documento e envie uma nova versão pelo sistema Climb.";

        emailService.enviarEmail(obterEmailEmpresa(documento), assunto, corpo);
    }

    private String obterEmailEmpresa(Documento documento) {
        if (documento.getEmpresa() == null) {
            return null;
        }

        return documento.getEmpresa().getEmail();
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

    private String valorOuPadrao(String valor, String padrao) {
        return StringUtils.hasText(valor) ? valor : padrao;
    }
}