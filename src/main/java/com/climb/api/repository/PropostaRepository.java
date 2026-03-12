package com.climb.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climb.api.model.Proposta;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    List<Proposta> findByStatus(String status);

}