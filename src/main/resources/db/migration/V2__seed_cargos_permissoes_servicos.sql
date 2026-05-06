-- Dados fixos baseados no Documento de Requisitos Climbe Investimentos - UNIT RSIV
-- Requisitos de Domínio: Cargos, Permissões e Serviços

-- ============================================
-- 1. CARGOS (Requisitos de Domínio 1)
-- ============================================
INSERT INTO cargos (nome) VALUES
('Compliance'),
('CEO'),
('Membro do Conselho'),
('CSO'),
('CMO'),
('CFO'),
('Analista de Valores Imobiliários - Trainee'),
('Analista de Valores Imobiliários - Junior'),
('Analista de Valores Imobiliários - Pleno'),
('Analista de Valores Imobiliários - Sênior'),
('Analista de BPO Financeiro'),
('Contador');

-- ============================================
-- 2. PERMISSÕES (Requisitos de Domínio 2)
-- codigo: identificador único para uso como enum no código
-- ============================================
ALTER TABLE permissoes ADD COLUMN codigo VARCHAR(100) UNIQUE NOT NULL;

INSERT INTO permissoes (descricao, codigo) VALUES
('Visualização, criação, edição e exclusão de Contratos', 'CONTRATO_CRUD'),
('Visualização, criação, edição e exclusão de cargos', 'CARGO_CRUD'),
('Visualização, criação, edição e exclusão de documentos jurídicos', 'DOCUMENTO_JURIDICO_CRUD'),
('Aplicação de nível de complexidade de contratos', 'CONTRATO_NIVEL_COMPLEXIDADE'),
('Edição restrita da planilha com necessidade de solicitar permissão', 'PLANILHA_EDICAO_RESTRITA'),
('Agendamento de Reuniões', 'REUNIAO_AGENDAMENTO'),
('Visualização, criação, edição e exclusão de relatórios', 'RELATORIO_CRUD'),
('Upload de arquivos', 'ARQUIVO_UPLOAD'),
('Download de arquivos', 'ARQUIVO_DOWNLOAD');

-- ============================================
-- 3. SERVIÇOS (Requisitos de Domínio 3)
-- ============================================
INSERT INTO servicos (nome) VALUES
('Contabilidade'),
('Avaliações de Empresas (Valuation)'),
('Terceirização de Rotinas Financeiras (BPO)'),
('Diretoria Financeira Sob Demanda (CFO)'),
('Fusões & Aquisições (M&A)');
