package com.climb.api.controller;

import com.climb.api.model.Relatorio;
import com.climb.api.model.dto.RelatorioPdfDownloadDTO;
import com.climb.api.model.dto.RelatorioRequestDTO;
import com.climb.api.model.dto.RelatorioResponseDTO;
import com.climb.api.service.RelatorioService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @PostMapping(value = "/{id}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RelatorioResponseDTO> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadPdf(id, file));
    }

    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorContrato(
            @PathVariable Long contratoId
    ) {
        return ResponseEntity.ok(service.listarPorContrato(contratoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorIdResponse(id));
    }

    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> criar(
            @RequestBody RelatorioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody RelatorioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping("/{id}/visualizar-pdf")
    public ResponseEntity<byte[]> visualizarPdf(@PathVariable Long id) {
        RelatorioPdfDownloadDTO pdf = service.obterPdfInline(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(pdf.nomeArquivo()).build().toString())
                .body(pdf.conteudo());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Long id) {
        RelatorioPdfDownloadDTO pdf = service.obterPdfParaDownload(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(pdf.nomeArquivo()).build().toString())
                .body(pdf.conteudo());
    }

    @PostMapping("/{id}/exportar-pdf")
    public ResponseEntity<RelatorioResponseDTO> exportarPdf(@PathVariable Long id) {
        return ResponseEntity.ok(service.exportarPdfResponse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
