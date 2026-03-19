-- Cargos (sem dependências)
CREATE TABLE cargos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

-- Permissões (sem dependências)
CREATE TABLE permissoes (
    id_permissao BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255)
);

-- Serviços (sem dependências)
CREATE TABLE servicos (
    id_servico BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255)
);

-- Empresas (sem dependências)
CREATE TABLE empresas (
    id_empresa BIGINT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    cnpj VARCHAR(18) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(255),
    cep VARCHAR(255),
    telefone VARCHAR(50),
    email VARCHAR(255),
    representante_nome VARCHAR(255),
    representante_cpf VARCHAR(14),
    representante_contato VARCHAR(50)
);

-- Usuários (depende de cargos)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    contato VARCHAR(255) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL,
    cargo_id BIGINT,
    CONSTRAINT fk_usuario_cargo FOREIGN KEY (cargo_id) REFERENCES cargos(id)
);

-- Usuário Permissões (depende de usuarios e permissoes)
CREATE TABLE usuario_permissoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_permissao BIGINT NOT NULL,
    CONSTRAINT fk_usuario_permissao_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_usuario_permissao_permissao FOREIGN KEY (id_permissao) REFERENCES permissoes(id_permissao)
);

-- Propostas (depende de empresas e usuarios)
CREATE TABLE propostas (
    id_proposta BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    data_criacao DATE,
    CONSTRAINT fk_proposta_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id_empresa),
    CONSTRAINT fk_proposta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Contratos (depende de propostas)
CREATE TABLE contratos (
    id_contrato BIGINT AUTO_INCREMENT PRIMARY KEY,
    proposta_id BIGINT NOT NULL UNIQUE,
    data_inicio DATE,
    data_fim DATE,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_contrato_proposta FOREIGN KEY (proposta_id) REFERENCES propostas(id_proposta)
);

-- Documentos (depende de empresas e usuarios)
CREATE TABLE documentos (
    id_documento BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    tipo_documento VARCHAR(255),
    url VARCHAR(255),
    validado VARCHAR(255),
    analista_id BIGINT,
    CONSTRAINT fk_documento_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id_empresa),
    CONSTRAINT fk_documento_analista FOREIGN KEY (analista_id) REFERENCES usuarios(id)
);

-- Empresa Serviço (depende de empresas e servicos)
CREATE TABLE empresa_servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_empresa BIGINT NOT NULL,
    id_servico BIGINT NOT NULL,
    CONSTRAINT fk_empresa_servico_empresa FOREIGN KEY (id_empresa) REFERENCES empresas(id_empresa),
    CONSTRAINT fk_empresa_servico_servico FOREIGN KEY (id_servico) REFERENCES servicos(id_servico)
);

-- Reuniões (depende de empresas)
CREATE TABLE reunioes (
    id_reuniao BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    empresa_id BIGINT NOT NULL,
    data DATE,
    hora TIME,
    presencial BOOLEAN,
    local VARCHAR(255),
    pauta TEXT,
    status VARCHAR(255),
    CONSTRAINT fk_reuniao_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id_empresa)
);

-- Participantes Reunião (depende de reunioes e usuarios)
CREATE TABLE participantes_reuniao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reuniao BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_participante_reuniao FOREIGN KEY (id_reuniao) REFERENCES reunioes(id_reuniao),
    CONSTRAINT fk_participante_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Notificações (depende de usuarios)
CREATE TABLE notificacoes (
    id_notificacao BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    mensagem VARCHAR(255),
    data_envio DATE,
    tipo VARCHAR(255),
    CONSTRAINT fk_notificacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Planilhas (depende de contratos)
CREATE TABLE planilhas (
    id_planilha BIGINT AUTO_INCREMENT PRIMARY KEY,
    contrato_id BIGINT NOT NULL,
    url_google_sheets VARCHAR(255),
    bloqueada BOOLEAN,
    permissao_visualizacao VARCHAR(255),
    CONSTRAINT fk_planilha_contrato FOREIGN KEY (contrato_id) REFERENCES contratos(id_contrato)
);

-- Relatórios (depende de contratos)
CREATE TABLE relatorios (
    id_relatorio BIGINT AUTO_INCREMENT PRIMARY KEY,
    contrato_id BIGINT NOT NULL,
    url_pdf VARCHAR(255),
    data_envio DATE,
    CONSTRAINT fk_relatorio_contrato FOREIGN KEY (contrato_id) REFERENCES contratos(id_contrato)
);
