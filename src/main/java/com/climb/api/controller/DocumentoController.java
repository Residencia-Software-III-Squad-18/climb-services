package com.climb.api.controller;

import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.model.dto.DocumentoSolicitacaoRequestDTO;
import com.climb.api.model.dto.DocumentoValidacaoRequestDTO;
import com.climb.api.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }


    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listar() {
        List<DocumentoResponseDTO> lista = documentoService.listar();
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DocumentoResponseDTO>> listarPorEmpresa(
            @PathVariable Long empresaId) {
        List<DocumentoResponseDTO> lista = documentoService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        DocumentoResponseDTO documento = documentoService.buscarPorId(id);
        return ResponseEntity.ok(documento);
    }


    @PostMapping("/solicitar")
    public ResponseEntity<DocumentoResponseDTO> solicitar(
            @Valid @RequestBody DocumentoSolicitacaoRequestDTO dto) {
        DocumentoResponseDTO criado = documentoService.solicitar(dto);
        return ResponseEntity.status(201).body(criado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id) {
        documentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<DocumentoResponseDTO> validar(
            @PathVariable Long id,
            @RequestBody DocumentoValidacaoRequestDTO dto) {
        DocumentoResponseDTO atualizado = documentoService.validar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
}