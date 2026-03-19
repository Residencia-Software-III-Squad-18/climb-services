package com.climb.api.service;

import com.climb.api.model.Reuniao;
import com.climb.api.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReuniaoService {

    private final ReuniaoRepository repository;

    public ReuniaoService(ReuniaoRepository repository) {
        this.repository = repository;
    }

    public List<Reuniao> listar() {
        return repository.findAll();
    }

    public Reuniao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reunião não encontrada"));
    }

    public List<Reuniao> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaIdEmpresa(empresaId);
    }

    public Reuniao criar(Reuniao reuniao) {

        if (reuniao.getTitulo() == null || reuniao.getTitulo().isEmpty()) {
            throw new RuntimeException("Título é obrigatório");
        }

        if (reuniao.getEmpresa() == null) {
            throw new RuntimeException("Empresa é obrigatória");
        }

        return repository.save(reuniao);
    }

    public Reuniao atualizar(Long id, Reuniao atualizada) {

        Reuniao reuniao = buscarPorId(id);

        reuniao.setTitulo(atualizada.getTitulo());
        reuniao.setEmpresa(atualizada.getEmpresa());
        reuniao.setData(atualizada.getData());
        reuniao.setHora(atualizada.getHora());
        reuniao.setPresencial(atualizada.getPresencial());
        reuniao.setLocal(atualizada.getLocal());
        reuniao.setPauta(atualizada.getPauta());
        reuniao.setStatus(atualizada.getStatus());

        return repository.save(reuniao);
    }

    public void deletar(Long id) {
        Reuniao reuniao = buscarPorId(id);
        repository.delete(reuniao);
    }
}