package com.climb.api.controller;

import com.climb.api.model.Documento;
import com.climb.api.service.DocumentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Documento> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Documento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Documento criar(@RequestBody Documento documento) {
        return service.criar(documento);
    }

    @PutMapping("/{id}")
    public Documento atualizar(@PathVariable Long id, @RequestBody Documento atualizado) {
        return service.atualizar(id, atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}