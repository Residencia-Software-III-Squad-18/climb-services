package com.climb.api.repository;

import com.climb.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioLoginRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}

