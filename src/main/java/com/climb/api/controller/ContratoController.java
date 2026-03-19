package com.climb.api.controller;

import com.climb.api.model.Contrato;
import com.climb.api.service.ContratoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Contrato> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Contrato buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/status/{status}")
    public List<Contrato> listarPorStatus(@PathVariable String status) {
        return service.listarPorStatus(status);
    }

    @PostMapping
    public Contrato criar(@RequestBody Contrato contrato) {
        return service.criar(contrato);
    }

    @PutMapping("/{id}")
    public Contrato atualizar(@PathVariable Long id, @RequestBody Contrato atualizado) {
        return service.atualizar(id, atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}