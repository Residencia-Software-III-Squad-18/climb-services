package com.climb.api.controller;

import com.climb.api.model.Relatorio;
import com.climb.api.model.dto.RelatorioPdfDownloadDTO;
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
    public Relatorio uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return service.uploadPdf(id, file);
    }

    @GetMapping
    public List<Relatorio> listar() {
        return service.listar();
    }

    @GetMapping("/contrato/{contratoId}")
    public List<Relatorio> listarPorContrato(@PathVariable Long contratoId) {
        return service.listarPorContrato(contratoId);
    }

    @GetMapping("/{id}")
    public Relatorio buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Relatorio criar(@RequestBody Relatorio relatorio) {
        return service.criar(relatorio);
    }

    @PutMapping("/{id}")
    public Relatorio atualizar(@PathVariable Long id, @RequestBody Relatorio atualizado) {
        return service.atualizar(id, atualizado);
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
    public Relatorio exportarPdf(@PathVariable Long id) {
        return service.exportarPdf(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
