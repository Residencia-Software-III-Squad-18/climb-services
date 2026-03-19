package com.climb.api.controller;

import com.climb.api.model.Proposta;
import com.climb.api.service.PropostaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

    private final PropostaService service;

    public PropostaController(PropostaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Proposta> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Proposta buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/status/{status}")
    public List<Proposta> listarPorStatus(@PathVariable String status) {
        return service.listarPorStatus(status);
    }

    @PostMapping
    public Proposta criar(@RequestBody Proposta proposta) {
        return service.criar(proposta);
    }

    @PutMapping("/{id}")
    public Proposta atualizar(@PathVariable Long id, @RequestBody Proposta atualizada) {
        return service.atualizar(id, atualizada);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}