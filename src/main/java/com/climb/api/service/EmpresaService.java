package com.climb.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climb.api.model.Empresa;
import com.climb.api.repository.EmpresaRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public List<Empresa> listar() {
        return repository.findAll();
    }

    public Empresa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    public Empresa criar(Empresa empresa) {

        if (empresa.getRazaoSocial() == null || empresa.getRazaoSocial().isEmpty()) {
            throw new RuntimeException("Razão social é obrigatória");
        }

        if (empresa.getNomeFantasia() == null || empresa.getNomeFantasia().isEmpty()) {
            throw new RuntimeException("Nome fantasia é obrigatório");
        }

        if (empresa.getCnpj() == null || empresa.getCnpj().isEmpty()) {
            throw new RuntimeException("CNPJ é obrigatório");
        }

        if (repository.findByCnpj(empresa.getCnpj()).isPresent()) {
            throw new RuntimeException("CNPJ já cadastrado");
        }

        return repository.save(empresa);
    }

    public Empresa atualizar(Long id, Empresa atualizada) {

        Empresa empresa = buscarPorId(id);

        repository.findByCnpj(atualizada.getCnpj()).ifPresent(e -> {
            if (!e.getIdEmpresa().equals(id)) {
                throw new RuntimeException("CNPJ já em uso");
            }
        });

        empresa.setRazaoSocial(atualizada.getRazaoSocial());
        empresa.setNomeFantasia(atualizada.getNomeFantasia());
        empresa.setCnpj(atualizada.getCnpj());
        empresa.setLogradouro(atualizada.getLogradouro());
        empresa.setNumero(atualizada.getNumero());
        empresa.setBairro(atualizada.getBairro());
        empresa.setCidade(atualizada.getCidade());
        empresa.setUf(atualizada.getUf());
        empresa.setCep(atualizada.getCep());
        empresa.setTelefone(atualizada.getTelefone());
        empresa.setEmail(atualizada.getEmail());
        empresa.setRepresentanteNome(atualizada.getRepresentanteNome());
        empresa.setRepresentanteCpf(atualizada.getRepresentanteCpf());
        empresa.setRepresentanteContato(atualizada.getRepresentanteContato());

        return repository.save(empresa);
    }

    public void deletar(Long id) {
        Empresa empresa = buscarPorId(id);
        repository.delete(empresa);
    }
}