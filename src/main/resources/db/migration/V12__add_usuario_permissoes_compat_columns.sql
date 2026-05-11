SET @usuario_id_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'usuario_id'
);

SET @add_usuario_id = IF(
    @usuario_id_exists = 0,
    'ALTER TABLE usuario_permissoes ADD COLUMN usuario_id BIGINT NULL AFTER id_permissao',
    'SELECT 1'
);

PREPARE add_usuario_id_stmt FROM @add_usuario_id;
EXECUTE add_usuario_id_stmt;
DEALLOCATE PREPARE add_usuario_id_stmt;

UPDATE usuario_permissoes
SET usuario_id = id_usuario
WHERE usuario_id IS NULL;

ALTER TABLE usuario_permissoes MODIFY COLUMN usuario_id BIGINT NOT NULL;

SET @permissao_id_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'permissao_id'
);

SET @add_permissao_id = IF(
    @permissao_id_exists = 0,
    'ALTER TABLE usuario_permissoes ADD COLUMN permissao_id BIGINT NULL AFTER usuario_id',
    'SELECT 1'
);

PREPARE add_permissao_id_stmt FROM @add_permissao_id;
EXECUTE add_permissao_id_stmt;
DEALLOCATE PREPARE add_permissao_id_stmt;

UPDATE usuario_permissoes
SET permissao_id = id_permissao
WHERE permissao_id IS NULL;

ALTER TABLE usuario_permissoes MODIFY COLUMN permissao_id BIGINT NOT NULL;
