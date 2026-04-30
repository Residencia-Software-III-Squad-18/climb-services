package com.climb.api.service;

import com.climb.api.model.Empresa;
import com.climb.api.model.Proposta;
import com.climb.api.model.Usuario;
import com.climb.api.model.dto.PropostaRequestDTO;
import com.climb.api.model.dto.PropostaResponseDTO;
import com.climb.api.repository.EmpresaRepository;
import com.climb.api.repository.PropostaRepository;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PropostaService {

    private final PropostaRepository repository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    public PropostaService(PropostaRepository repository,
                           EmpresaRepository empresaRepository,
                           UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private PropostaResponseDTO toResponseDTO(Proposta proposta) {
        return new PropostaResponseDTO(
                proposta.getIdProposta(),
                proposta.getEmpresa() != null ? proposta.getEmpresa().getIdEmpresa() : null,
                proposta.getUsuario() != null ? proposta.getUsuario().getId() : null,
                proposta.getStatus(),
                proposta.getDataCriacao()
        );
    }

    private Empresa buscarEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<PropostaResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public PropostaResponseDTO buscarPorId(Long id) {
        Proposta proposta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposta não encontrada"));
        return toResponseDTO(proposta);
    }

    public List<PropostaResponseDTO> listarPorStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status é obrigatório");
        }

        return repository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public PropostaResponseDTO criar(PropostaRequestDTO dto) {
        validarStatus(dto.status());

        Proposta proposta = new Proposta();
        proposta.setEmpresa(buscarEmpresa(dto.empresaId()));
        proposta.setUsuario(buscarUsuario(dto.usuarioId()));
        proposta.setStatus(dto.status());
        proposta.setDataCriacao(dto.dataCriacao() != null ? dto.dataCriacao() : LocalDate.now());

        return toResponseDTO(repository.save(proposta));
    }

    public PropostaResponseDTO atualizar(Long id, PropostaRequestDTO dto) {
        validarStatus(dto.status());

        Proposta proposta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposta não encontrada"));

        proposta.setEmpresa(buscarEmpresa(dto.empresaId()));
        proposta.setUsuario(buscarUsuario(dto.usuarioId()));
        proposta.setStatus(dto.status());
        proposta.setDataCriacao(dto.dataCriacao() != null ? dto.dataCriacao() : proposta.getDataCriacao());

        return toResponseDTO(repository.save(proposta));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Proposta não encontrada");
        }
        repository.deleteById(id);
    }

    private void validarStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status é obrigatório");
        }
    }
}