package com.climb.api.controller;

import com.climb.api.model.Planilha;
import com.climb.api.service.PlanilhaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planilhas")
public class PlanilhaController {

    private final PlanilhaService service;

    public PlanilhaController(PlanilhaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Planilha> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Planilha buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Planilha criar(@RequestBody Planilha planilha) {
        return service.criar(planilha);
    }

    @PutMapping("/{id}")
    public Planilha atualizar(@PathVariable Long id, @RequestBody Planilha atualizada) {
        return service.atualizar(id, atualizada);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}