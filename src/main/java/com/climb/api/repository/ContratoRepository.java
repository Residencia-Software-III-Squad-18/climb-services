package com.climb.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climb.api.model.Contrato;
import com.climb.api.model.enums.ContratoStatus;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByStatus(ContratoStatus status);

    List<Contrato> findByDataFimBetween(LocalDate inicio, LocalDate fim);

}