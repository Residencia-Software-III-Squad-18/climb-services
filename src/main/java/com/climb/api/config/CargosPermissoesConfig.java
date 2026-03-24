package com.climb.api.config;

import com.climb.api.model.PermissaoCodigo;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CargosPermissoesConfig {

    public static final Map<String, Set<PermissaoCodigo>> PERMISSOES_POR_CARGO = new HashMap<>();

    static {
        PERMISSOES_POR_CARGO.put("Compliance", EnumSet.of(
                PermissaoCodigo.CONTRATO_CRUD,
                PermissaoCodigo.DOCUMENTO_JURIDICO_CRUD,
                PermissaoCodigo.RELATORIO_CRUD,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO
        ));

        PERMISSOES_POR_CARGO.put("CEO", EnumSet.allOf(PermissaoCodigo.class));

        PERMISSOES_POR_CARGO.put("Membro do Conselho", EnumSet.of(
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO
        ));

        PERMISSOES_POR_CARGO.put("CSO", EnumSet.of(
                PermissaoCodigo.CONTRATO_CRUD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO,
                PermissaoCodigo.RELATORIO_CRUD,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD
        ));

        PERMISSOES_POR_CARGO.put("CMO", EnumSet.of(
                PermissaoCodigo.REUNIAO_AGENDAMENTO,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD
        ));

        PERMISSOES_POR_CARGO.put("CFO", EnumSet.of(
                PermissaoCodigo.RELATORIO_CRUD,
                PermissaoCodigo.PLANILHA_EDICAO_RESTRITA,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO
        ));

        Set<PermissaoCodigo> permissoesAnalista = EnumSet.of(
                PermissaoCodigo.DOCUMENTO_JURIDICO_CRUD,
                PermissaoCodigo.PLANILHA_EDICAO_RESTRITA,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO
        );

        PERMISSOES_POR_CARGO.put("Analista de Valores Imobiliários - Trainee", permissoesAnalista);
        PERMISSOES_POR_CARGO.put("Analista de Valores Imobiliários - Junior", permissoesAnalista);
        PERMISSOES_POR_CARGO.put("Analista de Valores Imobiliários - Pleno", permissoesAnalista);
        PERMISSOES_POR_CARGO.put("Analista de Valores Imobiliários - Sênior", EnumSet.of(
                PermissaoCodigo.DOCUMENTO_JURIDICO_CRUD,
                PermissaoCodigo.PLANILHA_EDICAO_RESTRITA,
                PermissaoCodigo.RELATORIO_CRUD,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO,
                PermissaoCodigo.CONTRATO_NIVEL_COMPLEXIDADE
        ));

        PERMISSOES_POR_CARGO.put("Analista de BPO Financeiro", EnumSet.of(
                PermissaoCodigo.PLANILHA_EDICAO_RESTRITA,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD,
                PermissaoCodigo.REUNIAO_AGENDAMENTO
        ));

        PERMISSOES_POR_CARGO.put("Contador", EnumSet.of(
                PermissaoCodigo.DOCUMENTO_JURIDICO_CRUD,
                PermissaoCodigo.PLANILHA_EDICAO_RESTRITA,
                PermissaoCodigo.ARQUIVO_UPLOAD,
                PermissaoCodigo.ARQUIVO_DOWNLOAD
        ));
    }
}