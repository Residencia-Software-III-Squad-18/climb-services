package com.climb.api.service;

import com.climb.api.model.Servico;
import com.climb.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public List<Servico> listar() {
        return repository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Servico criar(Servico servico) {
        return repository.save(servico);
    }

    public Servico atualizar(Long id, Servico atualizado) {
        Servico servico = buscarPorId(id);
        servico.setNome(atualizado.getNome());
        return repository.save(servico);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}