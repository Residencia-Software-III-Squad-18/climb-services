package com.climb.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climb.api.model.Cargo;
import com.climb.api.model.Usuario;
import com.climb.api.repository.CargoRepository;
import com.climb.api.repository.UsuarioRepository;
import com.climb.api.model.dto.UsuarioRequestDTO;
import com.climb.api.model.dto.UsuarioResponseDTO;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CargoRepository cargoRepository;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, CargoRepository cargoRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.cargoRepository = cargoRepository;
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
    UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(usuario.getId());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setCpf(usuario.getCpf());
        dto.setEmail(usuario.getEmail());
        dto.setContato(usuario.getContato());
        dto.setSituacao(usuario.getSituacao());

        if (usuario.getCargo() != null) {
            dto.setCargoNome(usuario.getCargo().getNome());
        }

        return dto;
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<UsuarioResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO buscarPorIdDTO(Long id) {
        Usuario usuario = buscarPorId(id);
        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {

        if (dto.getNomeCompleto() == null || dto.getNomeCompleto().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (dto.getCpf() == null || dto.getCpf().isEmpty()) {
            throw new RuntimeException("CPF é obrigatório");
        }


        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (dto.getSenha() == null || dto.getSenha().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        if (repository.findByCpf(dto.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setCpf(dto.getCpf());
        usuario.setEmail(dto.getEmail());
        usuario.setContato(dto.getContato());

        String senhaHash = passwordEncoder.encode(dto.getSenha());
        usuario.setSenhaHash(senhaHash);

        if (dto.getSituacao() == null) {
            usuario.setSituacao("ATIVO");
        } else if (!dto.getSituacao().equals("ATIVO") && !dto.getSituacao().equals("INATIVO")) {
            throw new RuntimeException("Situação inválida");
        } else {
            usuario.setSituacao(dto.getSituacao());
        }

        if (dto.getCargoId() != null) {
            Cargo cargo = cargoRepository.findById(dto.getCargoId())
                    .orElseThrow(() -> new RuntimeException("Cargo não encontrado"));

            usuario.setCargo(cargo);
        } else {
            throw new RuntimeException("Cargo é obrigatório");
        }

        Usuario salvo = repository.save(usuario);

        return toResponseDTO(salvo);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {

        Usuario usuario = buscarPorId(id);

        repository.findByCpf(dto.getCpf()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new RuntimeException("CPF já em uso");
            }
        });

        repository.findByEmail(dto.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new RuntimeException("Email já em uso");
            }
        });

        if (dto.getCargoId() != null) {
            Cargo cargo = cargoRepository.findById(dto.getCargoId())
                    .orElseThrow(() -> new RuntimeException("Cargo não encontrado"));

            usuario.setCargo(cargo);
        }

        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setCpf(dto.getCpf());
        usuario.setEmail(dto.getEmail());
        usuario.setContato(dto.getContato());

        if (dto.getSituacao() != null) {
            if (!dto.getSituacao().equals("ATIVO") && !dto.getSituacao().equals("INATIVO")) {
                throw new RuntimeException("Situação inválida");
            }
            usuario.setSituacao(dto.getSituacao());
        }

        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            String senhaHash = passwordEncoder.encode(dto.getSenha());
            usuario.setSenhaHash(senhaHash);
        }

        Usuario atualizado = repository.save(usuario);

        return toResponseDTO(atualizado);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);
    }
}