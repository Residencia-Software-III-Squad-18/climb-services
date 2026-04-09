CREATE TABLE oauth2_pending_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nome VARCHAR(255),
    avatar_url VARCHAR(500),
    token_unico VARCHAR(255) NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    consumido BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_oauth2_pending_token UNIQUE (token_unico),
    CONSTRAINT uk_oauth2_pending_provider_user UNIQUE (provider, provider_user_id)
);
