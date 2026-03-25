package com.climb.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climb.api.model.Usuario;
import com.climb.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario criar(Usuario usuario) {

        if (usuario.getNomeCompleto() == null || usuario.getNomeCompleto().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (usuario.getCpf() == null || usuario.getCpf().isEmpty()) {
            throw new RuntimeException("CPF é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (repository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (usuario.getSituacao() != "ATIVO" && usuario.getSituacao() != "INATIVO") {
            usuario.setSituacao("ATIVO");
        }

        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario atualizado) {

        Usuario usuario = buscarPorId(id);

        repository.findByCpf(atualizado.getCpf()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new RuntimeException("CPF já em uso");
            }
        });

        repository.findByEmail(atualizado.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new RuntimeException("Email já em uso");
            }
        });

        usuario.setNomeCompleto(atualizado.getNomeCompleto());
        usuario.setCpf(atualizado.getCpf());
        usuario.setEmail(atualizado.getEmail());
        usuario.setContato(atualizado.getContato());
        usuario.setSenhaHash(atualizado.getSenhaHash());
        usuario.setSituacao(atualizado.getSituacao());
        usuario.setCargo(atualizado.getCargo());

        return repository.save(usuario);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);
    }
}