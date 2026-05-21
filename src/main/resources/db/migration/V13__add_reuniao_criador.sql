ALTER TABLE reunioes
    ADD COLUMN criador_id BIGINT;

UPDATE reunioes r
SET criador_id = (
    SELECT MIN(pr.id_usuario)
    FROM participantes_reuniao pr
    WHERE pr.id_reuniao = r.id_reuniao
)
WHERE criador_id IS NULL;

ALTER TABLE reunioes
    ADD CONSTRAINT fk_reuniao_criador FOREIGN KEY (criador_id) REFERENCES usuarios(id);
