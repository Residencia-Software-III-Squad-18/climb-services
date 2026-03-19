package com.climb.api.controller;

import com.climb.api.model.Notificacao;
import com.climb.api.service.NotificacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notificacao> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Notificacao buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Notificacao criar(@RequestBody Notificacao notificacao) {
        return service.criar(notificacao);
    }

    @PutMapping("/{id}")
    public Notificacao atualizar(@PathVariable Long id, @RequestBody Notificacao atualizada) {
        return service.atualizar(id, atualizada);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}