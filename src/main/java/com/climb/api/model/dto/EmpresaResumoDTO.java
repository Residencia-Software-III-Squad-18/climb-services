package com.climb.api.model.dto;

import com.climb.api.model.Empresa;
import lombok.Data;

@Data
public class EmpresaResumoDTO {

    private Long id;
    private Long idEmpresa;
    /** Alias usado pelo front (Agenda / normalização). */
    private String nome;
    private String nomeFantasia;
    private String razaoSocial;

    public static EmpresaResumoDTO from(Empresa e) {
        EmpresaResumoDTO d = new EmpresaResumoDTO();
        d.setId(e.getIdEmpresa());
        d.setIdEmpresa(e.getIdEmpresa());
        String nome = e.getNomeFantasia() != null && !e.getNomeFantasia().isBlank()
                ? e.getNomeFantasia()
                : e.getRazaoSocial();
        d.setNome(nome);
        d.setNomeFantasia(e.getNomeFantasia());
        d.setRazaoSocial(e.getRazaoSocial());
        return d;
    }
}
