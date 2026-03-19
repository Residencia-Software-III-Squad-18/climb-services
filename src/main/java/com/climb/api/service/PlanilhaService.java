package com.climb.api.service;

import com.climb.api.model.Planilha;
import com.climb.api.repository.PlanilhaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanilhaService {

    private final PlanilhaRepository repository;

    public PlanilhaService(PlanilhaRepository repository) {
        this.repository = repository;
    }

    public List<Planilha> listar() {
        return repository.findAll();
    }

    public Planilha buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Planilha criar(Planilha planilha) {
        return repository.save(planilha);
    }

    public Planilha atualizar(Long id, Planilha atualizada) {
        Planilha planilha = buscarPorId(id);
        planilha.setContrato(atualizada.getContrato());
        planilha.setUrlGoogleSheets(atualizada.getUrlGoogleSheets());
        planilha.setBloqueada(atualizada.getBloqueada());
        planilha.setPermissaoVisualizacao(atualizada.getPermissaoVisualizacao());
        return repository.save(planilha);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}