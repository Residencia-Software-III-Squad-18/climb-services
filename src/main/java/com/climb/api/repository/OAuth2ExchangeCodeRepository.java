package com.climb.api.repository;

import com.climb.api.model.OAuth2ExchangeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OAuth2ExchangeCodeRepository extends JpaRepository<OAuth2ExchangeCode, Long> {

    Optional<OAuth2ExchangeCode> findByCodeAndConsumidoFalse(String code);

    void deleteByExpiraEmBefore(LocalDateTime dataHora);
}
