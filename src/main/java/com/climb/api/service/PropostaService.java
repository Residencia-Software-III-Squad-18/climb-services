package com.climb.api.service;

import com.climb.api.model.Proposta;
import com.climb.api.repository.PropostaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private final PropostaRepository repository;

    public PropostaService(PropostaRepository repository) {
        this.repository = repository;
    }

    public List<Proposta> listar() {
        return repository.findAll();
    }

    public Proposta buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Proposta> listarPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public Proposta criar(Proposta proposta) {
        return repository.save(proposta);
    }

    public Proposta atualizar(Long id, Proposta atualizada) {
        Proposta proposta = buscarPorId(id);
        proposta.setEmpresa(atualizada.getEmpresa());
        proposta.setUsuario(atualizada.getUsuario());
        proposta.setStatus(atualizada.getStatus());
        proposta.setDataCriacao(atualizada.getDataCriacao());
        return repository.save(proposta);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}