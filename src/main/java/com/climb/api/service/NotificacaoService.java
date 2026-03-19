package com.climb.api.service;

import com.climb.api.model.Notificacao;
import com.climb.api.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

    public List<Notificacao> listar() {
        return repository.findAll();
    }

    public Notificacao buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Notificacao criar(Notificacao notificacao) {
        return repository.save(notificacao);
    }

    public Notificacao atualizar(Long id, Notificacao atualizada) {
        Notificacao notificacao = buscarPorId(id);
        notificacao.setUsuario(atualizada.getUsuario());
        notificacao.setMensagem(atualizada.getMensagem());
        notificacao.setDataEnvio(atualizada.getDataEnvio());
        notificacao.setTipo(atualizada.getTipo());
        return repository.save(notificacao);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}