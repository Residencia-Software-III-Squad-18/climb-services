package com.climb.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climb.api.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByStatus(String status);

}