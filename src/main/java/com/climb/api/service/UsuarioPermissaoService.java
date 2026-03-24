package com.climb.api.service;

import com.climb.api.model.Permissao;
import com.climb.api.model.Usuario;
import com.climb.api.model.UsuarioPermissao;
import com.climb.api.repository.PermissaoRepository;
import com.climb.api.repository.UsuarioPermissaoRepository;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioPermissaoService {

    private final UsuarioPermissaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PermissaoRepository permissaoRepository;

    public UsuarioPermissaoService(
            UsuarioPermissaoRepository repository,
            UsuarioRepository usuarioRepository,
            PermissaoRepository permissaoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.permissaoRepository = permissaoRepository;
    }

    public List<UsuarioPermissao> listar() {
        return repository.findAll();
    }

    public UsuarioPermissao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));
    }

    public List<UsuarioPermissao> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuario_Id(usuarioId);
    }

    public UsuarioPermissao criar(Long usuarioId, Long permissaoId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada"));

        if (repository.existsByUsuario_IdAndPermissao_IdPermissao(usuarioId, permissaoId)) {
            throw new RuntimeException("Usuário já possui essa permissão");
        }

        UsuarioPermissao usuarioPermissao = new UsuarioPermissao();
        usuarioPermissao.setUsuario(usuario);
        usuarioPermissao.setPermissao(permissao);

        return repository.save(usuarioPermissao);
    }
    
    public UsuarioPermissao atualizar(Long id, Long usuarioId, Long permissaoId) {

        UsuarioPermissao usuarioPermissao = buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada"));

        usuarioPermissao.setUsuario(usuario);
        usuarioPermissao.setPermissao(permissao);

        return repository.save(usuarioPermissao);
    }

    public void deletar(Long id) {
        UsuarioPermissao up = buscarPorId(id);
        repository.delete(up);
    }
}