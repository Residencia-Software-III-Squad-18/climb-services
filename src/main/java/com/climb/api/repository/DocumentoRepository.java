package com.climb.api.repository;

import com.climb.api.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByEmpresaIdEmpresa(Long empresaId);

}