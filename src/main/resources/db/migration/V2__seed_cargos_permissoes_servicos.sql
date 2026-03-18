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
-- ============================================
INSERT INTO permissoes (descricao) VALUES
-- Contratos
('Visualização de Contratos'),
('Criação de Contratos'),
('Edição de Contratos'),
('Exclusão de Contratos'),
-- Cargos
('Visualização de Cargos'),
('Criação de Cargos'),
('Edição de Cargos'),
('Exclusão de Cargos'),
-- Documentos jurídicos
('Visualização de Documentos Jurídicos'),
('Criação de Documentos Jurídicos'),
('Edição de Documentos Jurídicos'),
('Exclusão de Documentos Jurídicos'),
-- Relatórios
('Visualização de Relatórios'),
('Criação de Relatórios'),
('Edição de Relatórios'),
('Exclusão de Relatórios'),
-- Outras permissões
('Aplicação de nível de complexidade de contratos'),
('Edição restrita da planilha com necessidade de solicitar permissão'),
('Agendamento de Reuniões'),
('Upload de arquivos'),
('Download de arquivos');

-- ============================================
-- 3. SERVIÇOS (Requisitos de Domínio 3)
-- ============================================
INSERT INTO servicos (nome) VALUES
('Contabilidade'),
('Avaliações de Empresas (Valuation)'),
('Terceirização de Rotinas Financeiras (BPO)'),
('Diretoria Financeira Sob Demanda (CFO)'),
('Fusões & Aquisições (M&A)');
