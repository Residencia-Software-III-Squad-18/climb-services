package com.climb.api.service;

import com.climb.api.model.Contrato;
import com.climb.api.repository.ContratoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoService {

    private final ContratoRepository repository;

    public ContratoService(ContratoRepository repository) {
        this.repository = repository;
    }

    public List<Contrato> listar() {
        return repository.findAll();
    }

    public Contrato buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Contrato> listarPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public Contrato criar(Contrato contrato) {
        return repository.save(contrato);
    }

    public Contrato atualizar(Long id, Contrato atualizado) {
        Contrato contrato = buscarPorId(id);
        contrato.setDataInicio(atualizado.getDataInicio());
        contrato.setDataFim(atualizado.getDataFim());
        contrato.setStatus(atualizado.getStatus());
        return repository.save(contrato);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}