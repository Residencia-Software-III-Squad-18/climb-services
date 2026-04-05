-- Garante que o login documentado funcione em bancos já existentes
INSERT INTO usuarios (nome_completo, cpf, email, contato, senha_hash, situacao, cargo_id)
VALUES (
    'Usuário de Teste',
    '12345678900',
    'teste@climb.com',
    '11999999999',
    '$2a$10$LrwTk5TUE7ER/IPGaOioc.zkW9IcGkTarqtEplDWGxXQ17OYZxZI6',
    'ATIVO',
    1
)
ON DUPLICATE KEY UPDATE
    nome_completo = VALUES(nome_completo),
    contato = VALUES(contato),
    senha_hash = VALUES(senha_hash),
    situacao = VALUES(situacao),
    cargo_id = VALUES(cargo_id);