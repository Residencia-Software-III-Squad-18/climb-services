package com.climb.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from:no-reply@climb.com}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    public void enviarEmailBoasVindas(String emailDestino, String nomeUsuario) {
        enviarEmail(
                emailDestino,
                "Bem-vindo ao sistema Climb!",
                "Ola, " + nomeUsuario + "!\n\nSeu cadastro foi realizado com sucesso.\n"
                        + "As instrucoes de acesso foram enviadas para este e-mail."
        );
    }

    public void enviarEmail(String emailDestino, String assunto, String texto) {
        if (emailDestino == null || emailDestino.isBlank()) {
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(emailDestino);
            mensagem.setSubject(assunto);
            mensagem.setText(texto);
            mailSender.send(mensagem);
        } catch (MailException ignored) {
            // Email delivery must not block the main business operation.
        }
    }
}
