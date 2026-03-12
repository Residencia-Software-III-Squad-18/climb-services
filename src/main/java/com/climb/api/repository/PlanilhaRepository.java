package com.climb.api.repository;

import com.climb.api.model.Planilha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanilhaRepository extends JpaRepository<Planilha, Long> {

    List<Planilha> findByContratoIdContrato(Long contratoId);

}