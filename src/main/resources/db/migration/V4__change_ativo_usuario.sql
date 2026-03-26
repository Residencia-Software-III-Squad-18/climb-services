-- 1. Adiciona nova coluna
ALTER TABLE usuarios 
ADD COLUMN situacao VARCHAR(50);

-- 2. Migra os dados antigos
UPDATE usuarios 
SET situacao = CASE 
    WHEN ativo = true THEN 'ATIVO'
    ELSE 'INATIVO'
END;

-- 3. Remove coluna antiga
ALTER TABLE usuarios 
DROP COLUMN ativo;