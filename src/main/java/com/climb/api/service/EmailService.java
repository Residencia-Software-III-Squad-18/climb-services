package com.climb.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailBoasVindas(String emailDestino, String nomeUsuario) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        
        mensagem.setFrom("no-reply@climb.com");
        mensagem.setTo(emailDestino);
        mensagem.setSubject("Bem-vindo ao sistema Climb!");
        mensagem.setText("Olá, " + nomeUsuario + "!\n\nSeu cadastro foi realizado com sucesso.\n" +
        "As instruções de acesso foram enviadas para este e-mail.");
        mailSender.send(mensagem);
    }
}