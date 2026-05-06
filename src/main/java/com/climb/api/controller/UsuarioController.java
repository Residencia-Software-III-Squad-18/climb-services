package com.climb.api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.climb.api.model.dto.UsuarioRequestDTO;
import com.climb.api.model.dto.UsuarioResponseDTO;
import com.climb.api.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }

    // Endpoints para administração de solicitações de acesso
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pendentes")
    public List<UsuarioResponseDTO> listarUsuariosPendentes() {
        return service.listarUsuariosPendentes();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorIdDTO(id);
    }

    @PostMapping
    public String criar(@RequestBody UsuarioRequestDTO dto) {
        return service.criarSolicitacaoAcesso(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id,
                                        @RequestBody UsuarioRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/aprovar")
    public UsuarioResponseDTO aprovarUsuario(@PathVariable Long id) {
        return service.aprovarUsuario(id);
    }
}