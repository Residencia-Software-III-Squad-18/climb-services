package com.climb.api.service;

import com.climb.api.model.Relatorio;
import com.climb.api.repository.RelatorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository repository;

    public RelatorioService(RelatorioRepository repository) {
        this.repository = repository;
    }

    public List<Relatorio> listar() {
        return repository.findAll();
    }

    public Relatorio buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Relatorio criar(Relatorio relatorio) {
        return repository.save(relatorio);
    }

    public Relatorio atualizar(Long id, Relatorio atualizado) {
        Relatorio relatorio = buscarPorId(id);
        relatorio.setContrato(atualizado.getContrato());
        relatorio.setUrlPdf(atualizado.getUrlPdf());
        relatorio.setDataEnvio(atualizado.getDataEnvio());
        return repository.save(relatorio);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}