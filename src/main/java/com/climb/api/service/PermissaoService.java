package com.climb.api.service;

import com.climb.api.model.Permissao;
import com.climb.api.repository.PermissaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissaoService {

    private final PermissaoRepository repository;

    public PermissaoService(PermissaoRepository repository) {
        this.repository = repository;
    }

    public List<Permissao> listar() {
        return repository.findAll();
    }

    public Permissao buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Permissao criar(Permissao permissao) {
        return repository.save(permissao);
    }

    public Permissao atualizar(Long id, Permissao atualizada) {
        Permissao permissao = buscarPorId(id);
        permissao.setDescricao(atualizada.getDescricao());
        return repository.save(permissao);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}