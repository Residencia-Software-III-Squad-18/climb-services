package com.climb.api.repository;

import com.climb.api.model.UsuarioPermissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long> {

    List<UsuarioPermissao> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndPermissao_IdPermissao(Long usuarioId, Long permissaoId);
}