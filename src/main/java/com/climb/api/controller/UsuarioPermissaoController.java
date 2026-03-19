package com.climb.api.controller;

import com.climb.api.model.UsuarioPermissao;
import com.climb.api.repository.UsuarioPermissaoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario-permissoes")
public class UsuarioPermissaoController {

    private final UsuarioPermissaoRepository repository;

    public UsuarioPermissaoController(UsuarioPermissaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UsuarioPermissao> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioPermissao buscarPorId(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public UsuarioPermissao criar(@RequestBody UsuarioPermissao usuarioPermissao) {
        return repository.save(usuarioPermissao);
    }

    @PutMapping("/{id}")
    public UsuarioPermissao atualizar(@PathVariable Long id, @RequestBody UsuarioPermissao atualizado) {

        UsuarioPermissao usuarioPermissao = repository.findById(id).orElseThrow();

        usuarioPermissao.setUsuario(atualizado.getUsuario());
        usuarioPermissao.setPermissao(atualizado.getPermissao());

        return repository.save(usuarioPermissao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }

}