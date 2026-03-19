package com.climb.api.service;

import com.climb.api.model.Documento;
import com.climb.api.repository.DocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoService {

    private final DocumentoRepository repository;

    public DocumentoService(DocumentoRepository repository) {
        this.repository = repository;
    }

    public List<Documento> listar() {
        return repository.findAll();
    }

    public Documento buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Documento criar(Documento documento) {
        return repository.save(documento);
    }

    public Documento atualizar(Long id, Documento atualizado) {
        Documento documento = buscarPorId(id);
        documento.setTipoDocumento(atualizado.getTipoDocumento());
        documento.setUrl(atualizado.getUrl());
        documento.setValidado(atualizado.getValidado());
        return repository.save(documento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}