CREATE TABLE oauth2_exchange_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    access_token VARCHAR(2048) NOT NULL,
    refresh_token VARCHAR(2048),
    google_access_token VARCHAR(2048),
    google_refresh_token VARCHAR(2048),
    expires_in BIGINT,
    user_id BIGINT,
    user_email VARCHAR(255),
    user_name VARCHAR(255),
    user_status VARCHAR(50),
    user_role VARCHAR(100),
    expira_em TIMESTAMP NOT NULL,
    consumido BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_oauth2_exchange_codes_code ON oauth2_exchange_codes(code);
CREATE INDEX idx_oauth2_exchange_codes_expira_em ON oauth2_exchange_codes(expira_em);
