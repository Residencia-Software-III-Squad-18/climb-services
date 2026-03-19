package com.climb.api.controller;

import com.climb.api.model.ParticipanteReuniao;
import com.climb.api.repository.ParticipanteReuniaoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participantes-reuniao")
public class ParticipanteReuniaoController {

    private final ParticipanteReuniaoRepository repository;

    public ParticipanteReuniaoController(ParticipanteReuniaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ParticipanteReuniao> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ParticipanteReuniao buscarPorId(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public ParticipanteReuniao criar(@RequestBody ParticipanteReuniao participante) {
        return repository.save(participante);
    }

    @PutMapping("/{id}")
    public ParticipanteReuniao atualizar(@PathVariable Long id, @RequestBody ParticipanteReuniao atualizado) {

        ParticipanteReuniao participante = repository.findById(id).orElseThrow();

        participante.setReuniao(atualizado.getReuniao());
        participante.setUsuario(atualizado.getUsuario());

        return repository.save(participante);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }

}