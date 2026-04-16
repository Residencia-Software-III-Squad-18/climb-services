package com.climb.api.controller;

import com.climb.api.model.Reuniao;
import com.climb.api.service.ReuniaoService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
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
    public List<Reuniao> listar() {
        return service.listar();
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
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) throws Exception {

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return service.criar(reuniao, accessToken);
    }

    @PutMapping("/{id}")
    public Reuniao atualizar(@PathVariable Long id, @RequestBody Reuniao atualizada) {
        return service.atualizar(id, atualizada);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}