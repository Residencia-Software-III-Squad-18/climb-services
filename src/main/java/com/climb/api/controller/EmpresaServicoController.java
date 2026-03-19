package com.climb.api.controller;

import com.climb.api.model.EmpresaServico;
import com.climb.api.repository.EmpresaServicoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresa-servicos")
public class EmpresaServicoController {

    private final EmpresaServicoRepository empresaServicoRepository;

    public EmpresaServicoController(EmpresaServicoRepository empresaServicoRepository) {
        this.empresaServicoRepository = empresaServicoRepository;
    }

    @GetMapping
    public List<EmpresaServico> listar() {
        return empresaServicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public EmpresaServico buscarPorId(@PathVariable Long id) {
        return empresaServicoRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public EmpresaServico criar(@RequestBody EmpresaServico empresaServico) {
        return empresaServicoRepository.save(empresaServico);
    }

    @PutMapping("/{id}")
    public EmpresaServico atualizar(@PathVariable Long id, @RequestBody EmpresaServico atualizado) {

        EmpresaServico empresaServico = empresaServicoRepository.findById(id).orElseThrow();

        empresaServico.setEmpresa(atualizado.getEmpresa());
        empresaServico.setServico(atualizado.getServico());

        return empresaServicoRepository.save(empresaServico);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        empresaServicoRepository.deleteById(id);
    }

}