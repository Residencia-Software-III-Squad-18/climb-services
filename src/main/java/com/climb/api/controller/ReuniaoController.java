package com.climb.api.controller;

import com.climb.api.model.Reuniao;
import com.climb.api.service.ReuniaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reunioes")
public class ReuniaoController {

    private final ReuniaoService service;

    public ReuniaoController(ReuniaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reuniao> listar(
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken) {
        return service.listar(googleAccessToken);
    }

    @GetMapping("/{id}")
    public Reuniao buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Reuniao> listarPorEmpresa(@PathVariable Long empresaId) {
        return service.listarPorEmpresa(empresaId);
    }

    @PostMapping
    public Reuniao criar(
            @RequestBody Reuniao reuniao,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken) throws Exception {
        return service.criar(reuniao, googleAccessToken);
    }

    @PutMapping("/{id}")
    public Reuniao atualizar(
            @PathVariable Long id,
            @RequestBody Reuniao atualizada,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken) {
        return service.atualizar(id, atualizada, googleAccessToken);
    }

    @DeleteMapping("/{id}")
    public void deletar(
            @PathVariable Long id,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken) {
        service.deletar(id, googleAccessToken);
    }
}
