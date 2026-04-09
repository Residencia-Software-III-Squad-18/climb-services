package com.climb.api.repository;

import com.climb.api.model.OAuthProvider;
import com.climb.api.model.UsuarioOAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioOAuthRepository extends JpaRepository<UsuarioOAuth, Long> {

    Optional<UsuarioOAuth> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    Optional<UsuarioOAuth> findByUsuarioIdAndProvider(Long usuarioId, OAuthProvider provider);

    boolean existsByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
