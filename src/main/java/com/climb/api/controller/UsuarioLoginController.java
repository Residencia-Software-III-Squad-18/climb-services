package com.climb.api.controller;

import com.climb.api.model.dto.UsuarioLoginRequestDTO;
import com.climb.api.model.dto.UsuarioLoginResponseDTO;
import com.climb.api.service.UsuarioLoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UsuarioLoginController {
    private final UsuarioLoginService service;

    public UsuarioLoginController(UsuarioLoginService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponseDTO> login(
            @RequestBody @Valid UsuarioLoginRequestDTO dto) {

        UsuarioLoginResponseDTO response = service.login(dto);

        return ResponseEntity.ok(response);
    }
}
