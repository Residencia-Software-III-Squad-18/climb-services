package com.climb.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.climb.api.model.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    Optional<Cargo> findByNome(String nome);

}