SET @usuario_id_fk = (
    SELECT MIN(constraint_name)
    FROM information_schema.key_column_usage
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'usuario_id'
      AND referenced_table_name IS NOT NULL
);

SET @drop_usuario_id_fk = IF(
    @usuario_id_fk IS NOT NULL,
    CONCAT('ALTER TABLE usuario_permissoes DROP FOREIGN KEY ', @usuario_id_fk),
    'SELECT 1'
);

PREPARE drop_usuario_id_fk_stmt FROM @drop_usuario_id_fk;
EXECUTE drop_usuario_id_fk_stmt;
DEALLOCATE PREPARE drop_usuario_id_fk_stmt;

SET @usuario_id_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'usuario_id'
);

SET @drop_usuario_id = IF(
    @usuario_id_exists > 0,
    'ALTER TABLE usuario_permissoes DROP COLUMN usuario_id',
    'SELECT 1'
);

PREPARE drop_usuario_id_stmt FROM @drop_usuario_id;
EXECUTE drop_usuario_id_stmt;
DEALLOCATE PREPARE drop_usuario_id_stmt;

SET @permissao_id_fk = (
    SELECT MIN(constraint_name)
    FROM information_schema.key_column_usage
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'permissao_id'
      AND referenced_table_name IS NOT NULL
);

SET @drop_permissao_id_fk = IF(
    @permissao_id_fk IS NOT NULL,
    CONCAT('ALTER TABLE usuario_permissoes DROP FOREIGN KEY ', @permissao_id_fk),
    'SELECT 1'
);

PREPARE drop_permissao_id_fk_stmt FROM @drop_permissao_id_fk;
EXECUTE drop_permissao_id_fk_stmt;
DEALLOCATE PREPARE drop_permissao_id_fk_stmt;

SET @permissao_id_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'usuario_permissoes'
      AND column_name = 'permissao_id'
);

SET @drop_permissao_id = IF(
    @permissao_id_exists > 0,
    'ALTER TABLE usuario_permissoes DROP COLUMN permissao_id',
    'SELECT 1'
);

PREPARE drop_permissao_id_stmt FROM @drop_permissao_id;
EXECUTE drop_permissao_id_stmt;
DEALLOCATE PREPARE drop_permissao_id_stmt;
