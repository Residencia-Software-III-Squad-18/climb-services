package com.climb.api.controller;

import com.climb.api.model.Relatorio;
import com.climb.api.service.RelatorioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Relatorio> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Relatorio buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Relatorio criar(@RequestBody Relatorio relatorio) {
        return service.criar(relatorio);
    }

    @PutMapping("/{id}")
    public Relatorio atualizar(@PathVariable Long id, @RequestBody Relatorio atualizado) {
        return service.atualizar(id, atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}