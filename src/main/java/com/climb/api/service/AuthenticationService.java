package com.climb.api.service;

import com.climb.api.model.Usuario;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.model.dto.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO autenticar(String email, String senha) {
        // Find user by email
        Usuario usuario = usuarioService.buscarPorEmail(email);

        // Validate user exists
        if (usuario == null) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        // Validate user is active
        if (!"ATIVO".equals(usuario.getSituacao())) {
            throw new RuntimeException("Usuário inativo");
        }

        // Validate password
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new RuntimeException("senha inválida");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(usuario.getId(), usuario.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getId(), usuario.getEmail());

        // Build response
        UsuarioResponseDTO usuarioDTO = buildUsuarioResponseDTO(usuario);
        long expiresIn = jwtUtil.getAccessTokenExpirationTime();

        return new LoginResponseDTO(accessToken, refreshToken, usuarioDTO, expiresIn);
    }

    public String refreshAccessToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        // Check token type
        String tokenType = jwtUtil.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Token fornecido não é um refresh token");
        }

        // Extract user info and generate new access token
        Long usuarioId = jwtUtil.extractUserId(refreshToken);
        String email = jwtUtil.extractEmail(refreshToken);

        return jwtUtil.generateAccessToken(usuarioId, email);
    }

    private UsuarioResponseDTO buildUsuarioResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setCpf(usuario.getCpf());
        dto.setEmail(usuario.getEmail());
        dto.setContato(usuario.getContato());
        dto.setSituacao(usuario.getSituacao());

        if (usuario.getCargo() != null) {
            dto.setCargoNome(usuario.getCargo().getNome());
        }

        return dto;
    }
}
