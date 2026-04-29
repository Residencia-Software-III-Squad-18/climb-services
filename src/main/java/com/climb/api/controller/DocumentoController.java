package com.climb.api.controller;

import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.model.dto.DocumentoSolicitacaoRequestDTO;
import com.climb.api.model.dto.DocumentoValidacaoRequestDTO;
import com.climb.api.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
@Tag(name = "Documentos", description = "Operações internas de gestão documental — requer autenticação")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }


    @Operation(summary = "Listar todos os documentos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listar() {
        List<DocumentoResponseDTO> lista = documentoService.listar();
        return ResponseEntity.ok(lista);
    }


    @Operation(summary = "Listar documentos por empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada", content = @Content)
    })
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DocumentoResponseDTO>> listarPorEmpresa(
            @PathVariable Long empresaId) {
        List<DocumentoResponseDTO> lista = documentoService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(lista);
    }


    @Operation(summary = "Buscar documento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        DocumentoResponseDTO documento = documentoService.buscarPorId(id);
        return ResponseEntity.ok(documento);
    }

    @Operation(
            summary = "Solicitar documento",
            description = "O usuário interno solicita um documento para a empresa. O status inicial é definido automaticamente como PENDENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa ou analista não encontrado", content = @Content)
    })
    @PostMapping("/solicitar")
    public ResponseEntity<DocumentoResponseDTO> solicitar(
            @Valid @RequestBody DocumentoSolicitacaoRequestDTO dto) {
        DocumentoResponseDTO criado = documentoService.solicitar(dto);
        return ResponseEntity.status(201).body(criado);
    }

    @Operation(summary = "Deletar documento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Documento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id) {
        documentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validar documento",
            description = "O analista aprova ou rejeita o documento enviado pela empresa. Valores aceitos: APROVADO ou REPROVADO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento validado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content)
    })
    @PatchMapping("/{id}/validar")
    public ResponseEntity<DocumentoResponseDTO> validar(
            @PathVariable Long id,
            @RequestBody DocumentoValidacaoRequestDTO dto) {
        DocumentoResponseDTO atualizado = documentoService.validar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
}