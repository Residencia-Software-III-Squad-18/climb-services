CREATE TABLE usuario_oauth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email_provider VARCHAR(255),
    nome_provider VARCHAR(255),
    avatar_url VARCHAR(500),
    vinculado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_oauth_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT uk_usuario_oauth_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT uk_usuario_oauth_usuario_provider UNIQUE (usuario_id, provider)
);
