package com.climb.api.controller;

import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.model.dto.DocumentoSolicitacaoRequestDTO;
import com.climb.api.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/documentos/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PatchMapping(value = "/{id}/enviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> enviar(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        DocumentoResponseDTO atualizado = documentoService.enviar(id, arquivo);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id) {
        documentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}