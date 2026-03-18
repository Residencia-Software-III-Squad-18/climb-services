package com.climb.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climb.api.model.Reuniao;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    List<Reuniao> findByEmpresa_IdEmpresa(Long empresaId);

}