package com.climb.api.controller;

import com.climb.api.model.dto.RelatorioPdfDownloadDTO;
import com.climb.api.model.dto.RelatorioRequestDTO;
import com.climb.api.model.dto.RelatorioResponseDTO;
import com.climb.api.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "Endpoints internos de gestão de relatórios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @Operation(
            summary = "Enviar PDF do relatório",
            description = "Recebe um arquivo PDF via multipart/form-data, valida o arquivo e vincula o PDF ao relatório informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente, inválido ou corrompido"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao salvar o PDF do relatório")
    })
    @PostMapping(value = "/{id}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RelatorioResponseDTO> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadPdf(id, file));
    }

    @Operation(summary = "Listar todos os relatórios")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Listar relatórios por contrato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorContrato(
            @PathVariable Long contratoId
    ) {
        return ResponseEntity.ok(service.listarPorContrato(contratoId));
    }

    @Operation(summary = "Buscar relatório por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório encontrado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorIdResponse(id));
    }

    @Operation(
            summary = "Criar relatório",
            description = "Cria o registro do relatório no banco de dados a partir dos dados informados, como contrato e descrição. Este endpoint não gera o PDF automaticamente; os dados salvos serão usados posteriormente na exportação do relatório em PDF."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Contrato obrigatório ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Contrato não encontrado")
    })
    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> criar(
            @RequestBody RelatorioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @Operation(
            summary = "Atualizar relatório",
            description = "Atualiza os dados de um relatório. Caso exista PDF vinculado, ele será invalidado para permitir nova geração."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Relatório ou contrato não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody RelatorioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(
            summary = "Visualizar PDF do relatório",
            description = "Retorna o PDF do relatório para visualização inline no navegador. Caso o PDF ainda não exista, ele será gerado automaticamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Relatório sem dados obrigatórios para geração do PDF"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao carregar ou gerar o PDF")
    })
    @GetMapping("/{id}/visualizar-pdf")
    public ResponseEntity<byte[]> visualizarPdf(@PathVariable Long id) {
        RelatorioPdfDownloadDTO pdf = service.obterPdfInline(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(pdf.nomeArquivo()).build().toString())
                .body(pdf.conteudo());
    }

    @Operation(
            summary = "Baixar PDF do relatório",
            description = "Retorna o PDF do relatório como anexo para download. Caso o PDF ainda não exista, ele será gerado automaticamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF baixado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Relatório sem dados obrigatórios para geração do PDF"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao carregar ou gerar o PDF")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Long id) {
        RelatorioPdfDownloadDTO pdf = service.obterPdfParaDownload(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(pdf.nomeArquivo()).build().toString())
                .body(pdf.conteudo());
    }

    @Operation(
            summary = "Exportar relatório em PDF",
            description = "Gera o PDF de um relatório já existente a partir dos dados salvos no sistema, como descrição, contrato e empresa vinculada. O arquivo é criado com base no template JasperReports e o caminho gerado é salvo no campo urlPdf."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF exportado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Relatório sem dados obrigatórios para exportação"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar ou salvar o PDF")
    })
    @PostMapping("/{id}/exportar-pdf")
    public ResponseEntity<RelatorioResponseDTO> exportarPdf(@PathVariable Long id) {
        return ResponseEntity.ok(service.exportarPdfResponse(id));
    }

    @Operation(summary = "Deletar relatório")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Relatório deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}