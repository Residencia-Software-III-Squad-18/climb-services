package com.climb.api.service;

import com.climb.api.model.dto.UsuarioLoginRequestDTO;
import com.climb.api.model.dto.UsuarioLoginResponseDTO;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioLoginService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioLoginService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO dto) {
        var usuario = this.repository.findByEmail(dto.email()).orElseThrow(
                () -> new RuntimeException("Email/password incorrect")
        );

        var passwordMatches = this.passwordEncoder.matches(dto.senha(), usuario.getSenhaHash());

        if (!passwordMatches) {
            throw new RuntimeException("Email/password incorrect");
        }

        return new UsuarioLoginResponseDTO("Login realizado com sucesso");
    }
}