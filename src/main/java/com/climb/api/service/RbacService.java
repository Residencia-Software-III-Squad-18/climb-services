package com.climb.api.service;

import com.climb.api.model.PermissaoCodigo;
import com.climb.api.model.UsuarioPermissao;
import com.climb.api.repository.UsuarioPermissaoRepository;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RbacService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;

    public RbacService(
            UsuarioRepository usuarioRepository,
            UsuarioPermissaoRepository usuarioPermissaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPermissaoRepository = usuarioPermissaoRepository;
    }

    public Set<PermissaoCodigo> getPermissoesDoUsuario(Long usuarioId) {

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: id=" + usuarioId));

        Set<PermissaoCodigo> permissoes = EnumSet.noneOf(PermissaoCodigo.class);

        List<UsuarioPermissao> permissoesIndividuais =
                usuarioPermissaoRepository.findByUsuario_Id(usuarioId);

        for (UsuarioPermissao up : permissoesIndividuais) {
            try {
                PermissaoCodigo codigo = PermissaoCodigo.valueOf(up.getPermissao().getCodigo());
                permissoes.add(codigo);
            } catch (IllegalArgumentException e) {
                // Código inválido no banco — ignora
            }
        }

        return permissoes;
    }

    public boolean temPermissao(Long usuarioId, PermissaoCodigo permissao) {
        return getPermissoesDoUsuario(usuarioId).contains(permissao);
    }

    public boolean temTodasPermissoes(Long usuarioId, PermissaoCodigo... permissoes) {
        Set<PermissaoCodigo> permissoesDoUsuario = getPermissoesDoUsuario(usuarioId);
        for (PermissaoCodigo p : permissoes) {
            if (!permissoesDoUsuario.contains(p)) return false;
        }
        return true;
    }

    public boolean temAlgumaPermissao(Long usuarioId, PermissaoCodigo... permissoes) {
        Set<PermissaoCodigo> permissoesDoUsuario = getPermissoesDoUsuario(usuarioId);
        for (PermissaoCodigo p : permissoes) {
            if (permissoesDoUsuario.contains(p)) return true;
        }
        return false;
    }
}