package com.climb.api.service;

import com.climb.api.model.Notificacao;
import com.climb.api.model.Usuario;
import com.climb.api.model.dto.NotificacaoRequestDTO;
import com.climb.api.model.dto.NotificacaoResponseDTO;
import com.climb.api.repository.NotificacaoRepository;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<NotificacaoResponseDTO> listar(Long usuarioId) {
        return repository.findByUsuario_IdOrderByDataEnvioDesc(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public NotificacaoResponseDTO buscarPorId(Long id, Long usuarioId) {
        return repository.findByIdNotificacaoAndUsuario_Id(id, usuarioId)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Notificação não encontrada"));
    }

    public NotificacaoResponseDTO criar(NotificacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setTipo(dto.getTipo());
        notificacao.setDataEnvio(dto.getDataEnvio() != null ? dto.getDataEnvio() : LocalDate.now());

        return toResponseDTO(repository.save(notificacao));
    }

    public void deletar(Long id, Long usuarioId) {
        Notificacao notificacao = repository.findByIdNotificacaoAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Notificação não encontrada"));
        repository.delete(notificacao);
    }

    private NotificacaoResponseDTO toResponseDTO(Notificacao notificacao) {
        NotificacaoResponseDTO dto = new NotificacaoResponseDTO();
        dto.setIdNotificacao(notificacao.getIdNotificacao());
        dto.setUsuarioId(notificacao.getUsuario() != null ? notificacao.getUsuario().getId() : null);
        dto.setMensagem(notificacao.getMensagem());
        dto.setDataEnvio(notificacao.getDataEnvio());
        dto.setTipo(notificacao.getTipo());
        return dto;
    }

}