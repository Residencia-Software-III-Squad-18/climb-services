package com.climb.api.controller;

import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documentos")
public class DocumentoUploadController {

    private final DocumentoService documentoService;

    public DocumentoUploadController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }


    @PatchMapping(value = "/{id}/enviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> enviar(@PathVariable Long id, MultipartFile arquivo) {
        DocumentoResponseDTO atualizado = documentoService.enviar(id, arquivo);
        return ResponseEntity.ok(atualizado);
    }
}