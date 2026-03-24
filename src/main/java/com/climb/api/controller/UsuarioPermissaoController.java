package com.climb.api.controller;

import com.climb.api.model.UsuarioPermissao;
import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.UsuarioPermissaoDTO;
import com.climb.api.service.UsuarioPermissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario-permissoes")
public class UsuarioPermissaoController {

    private final UsuarioPermissaoService service;

    public UsuarioPermissaoController(UsuarioPermissaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioPermissao>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioPermissao>> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<UsuarioPermissao>>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorUsuario(usuarioId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioPermissao>> criar(@RequestBody UsuarioPermissaoDTO dto) {
        try {
            return ResponseEntity.status(201).body(ApiResponse.ok(
                service.criar(dto.getUsuarioId(), dto.getPermissaoId()),
                "Permissão associada com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioPermissao>> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioPermissaoDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                service.atualizar(id, dto.getUsuarioId(), dto.getPermissaoId()),
                "Permissão atualizada com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.ok(ApiResponse.ok(null, "Permissão removida com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}