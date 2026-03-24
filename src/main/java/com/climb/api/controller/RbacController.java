package com.climb.api.controller;

import com.climb.api.model.PermissaoCodigo;
import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.RbacDTO;
import com.climb.api.repository.UsuarioRepository;
import com.climb.api.service.RbacService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/rbac")
public class RbacController {

    private final RbacService rbacService;
    private final UsuarioRepository usuarioRepository;

    public RbacController(RbacService rbacService, UsuarioRepository usuarioRepository) {
        this.rbacService = rbacService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/usuario/{usuarioId}/permissoes")
    public ResponseEntity<ApiResponse<Set<PermissaoCodigo>>> getPermissoesDoUsuario(
            @PathVariable Long usuarioId) {
        try {
            Set<PermissaoCodigo> permissoes = rbacService.getPermissoesDoUsuario(usuarioId);
            return ResponseEntity.ok(ApiResponse.ok(permissoes));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}/permissoes/cargo")
    public ResponseEntity<ApiResponse<Set<PermissaoCodigo>>> getPermissoesDoCargo(
            @PathVariable Long usuarioId) {
        try {
            Set<PermissaoCodigo> permissoes = rbacService.getPermissoesDoCargo(usuarioId);
            return ResponseEntity.ok(ApiResponse.ok(permissoes));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}/tem-permissao/{permissao}")
    public ResponseEntity<ApiResponse<Boolean>> temPermissao(
            @PathVariable Long usuarioId,
            @PathVariable String permissao) {
        try {
            PermissaoCodigo codigo = PermissaoCodigo.valueOf(permissao);
            boolean resultado = rbacService.temPermissao(usuarioId, codigo);
            return ResponseEntity.ok(ApiResponse.ok(resultado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Permissão inválida: " + permissao));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}/perfil")
    public ResponseEntity<ApiResponse<RbacDTO>> getPerfilRbac(
            @PathVariable Long usuarioId) {
        try {
            Set<PermissaoCodigo> doCargo = rbacService.getPermissoesDoCargo(usuarioId);
            Set<PermissaoCodigo> efetivas = rbacService.getPermissoesDoUsuario(usuarioId);

            Set<PermissaoCodigo> individuais = new HashSet<>(efetivas);
            individuais.removeAll(doCargo);

            var usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            RbacDTO dto = new RbacDTO();
            dto.setUsuarioId(usuarioId);
            dto.setNomeUsuario(usuario.getNomeCompleto());
            dto.setNomeCargo(usuario.getCargo() != null ? usuario.getCargo().getNome() : null);
            dto.setPermissoesDoCargo(doCargo);
            dto.setPermissoesIndividuais(individuais);
            dto.setPermissoesEfetivas(efetivas);

            return ResponseEntity.ok(ApiResponse.ok(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}