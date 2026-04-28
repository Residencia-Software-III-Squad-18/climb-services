package com.climb.api.service;

import com.climb.api.mapper.DocumentoMapper;
import com.climb.api.model.Documento;
import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.model.dto.DocumentoSolicitacaoRequestDTO;
import com.climb.api.model.dto.DocumentoValidacaoRequestDTO;
import com.climb.api.repository.DocumentoRepository;
import com.climb.api.repository.EmpresaRepository;
import com.climb.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoMapper documentoMapper;

    public DocumentoService(DocumentoRepository documentoRepository, EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, DocumentoMapper documentoMapper) {
        this.documentoRepository = documentoRepository;
        this.empresaRepository   = empresaRepository;
        this.usuarioRepository   = usuarioRepository;
        this.documentoMapper     = documentoMapper;
    }

    public List<DocumentoResponseDTO> listar() {
        return documentoMapper.toResponseDto(documentoRepository.findAll());
    }

    public List<DocumentoResponseDTO> listarPorEmpresa(Long empresaId) {
        return documentoMapper.toResponseDto(
                documentoRepository.findByEmpresa_IdEmpresa(empresaId));
    }

    public DocumentoResponseDTO buscarPorId(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));
        return documentoMapper.toResponseDto(documento);
    }

    public DocumentoResponseDTO solicitar(DocumentoSolicitacaoRequestDTO dto) {
        Documento documento = documentoMapper.toEntity(dto);

        documento.setValidado("PENDENTE");
        documento.setEmpresa(empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empresa não encontrada: " + dto.empresaId())));
        documento.setAnalista(usuarioRepository.findById(dto.analistaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Analista não encontrado: " + dto.analistaId())));

        return documentoMapper.toResponseDto(documentoRepository.save(documento));
    }

    public DocumentoResponseDTO validar(Long id, DocumentoValidacaoRequestDTO dto) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));

        documento.setValidado(dto.validado());

        return documentoMapper.toResponseDto(documentoRepository.save(documento));
    }



    public void deletar(Long id) {
        if (!documentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Documento não encontrado: " + id);
        }
        documentoRepository.deleteById(id);
    }

    public DocumentoResponseDTO enviar(Long id, MultipartFile arquivo) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));

        documento.setUrl(salvarArquivo(arquivo));
        documento.setValidado("EM_ANALISE");

        return documentoMapper.toResponseDto(documentoRepository.save(documento));
    }

    // --- helpers ---

    private String salvarArquivo(MultipartFile arquivo) {
        try {
            Path pasta = Paths.get("uploads/documentos");

            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }

            String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path destino = pasta.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}