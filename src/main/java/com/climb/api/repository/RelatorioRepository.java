package com.climb.api.repository;

import com.climb.api.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    List<Relatorio> findByContrato_Id(Long contratoId);

}