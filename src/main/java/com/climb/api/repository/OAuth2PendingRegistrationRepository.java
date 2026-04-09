package com.climb.api.repository;

import com.climb.api.model.OAuth2PendingRegistration;
import com.climb.api.model.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OAuth2PendingRegistrationRepository extends JpaRepository<OAuth2PendingRegistration, Long> {

    Optional<OAuth2PendingRegistration> findByTokenUnicoAndConsumidoFalse(String tokenUnico);

    Optional<OAuth2PendingRegistration> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    void deleteByExpiraEmBefore(LocalDateTime dataHora);
}
