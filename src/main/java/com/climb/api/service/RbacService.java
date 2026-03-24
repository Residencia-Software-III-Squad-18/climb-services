package com.climb.api.service;

import com.climb.api.config.CargosPermissoesConfig;
import com.climb.api.model.PermissaoCodigo;
import com.climb.api.model.Usuario;
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

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: id=" + usuarioId));

        Set<PermissaoCodigo> permissoes = EnumSet.noneOf(PermissaoCodigo.class);

        if (usuario.getCargo() != null) {
            String nomeCargo = usuario.getCargo().getNome();
            Set<PermissaoCodigo> permissoesDoCargo =
                    CargosPermissoesConfig.PERMISSOES_POR_CARGO.get(nomeCargo);

            if (permissoesDoCargo != null) {
                permissoes.addAll(permissoesDoCargo);
            }
        }

        List<UsuarioPermissao> permissoesIndividuais =
                usuarioPermissaoRepository.findByUsuario_Id(usuarioId);

        for (UsuarioPermissao up : permissoesIndividuais) {
            try {
                PermissaoCodigo codigo = PermissaoCodigo.valueOf(up.getPermissao().getCodigo());
                permissoes.add(codigo);
            } catch (IllegalArgumentException e) {
            }
        }

        return permissoes;
    }

    public Set<PermissaoCodigo> getPermissoesDoCargo(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: id=" + usuarioId));

        if (usuario.getCargo() == null) {
            return EnumSet.noneOf(PermissaoCodigo.class);
        }

        String nomeCargo = usuario.getCargo().getNome();
        Set<PermissaoCodigo> permissoesDoCargo =
                CargosPermissoesConfig.PERMISSOES_POR_CARGO.get(nomeCargo);

        return permissoesDoCargo != null
                ? EnumSet.copyOf(permissoesDoCargo)
                : EnumSet.noneOf(PermissaoCodigo.class);
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