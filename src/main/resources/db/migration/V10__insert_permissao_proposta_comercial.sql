-- Permissão para propostas comerciais
-- Insere a permissão somente se ainda não existir (idempotente)
INSERT INTO permissoes (descricao, codigo)
SELECT 'Visualização, criação, edição e exclusão de Propostas Comerciais', 'PROPOSTA_CRUD'
WHERE NOT EXISTS (SELECT 1 FROM permissoes WHERE codigo = 'PROPOSTA_CRUD');
