package com.climb.api.repository;

import com.climb.api.model.EmpresaServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaServicoRepository extends JpaRepository<EmpresaServico, Long> {

    List<EmpresaServico> findByEmpresa_IdEmpresa(Long empresaId);

}