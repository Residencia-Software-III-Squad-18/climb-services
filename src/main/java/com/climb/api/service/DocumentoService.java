package com.climb.api.service;

import com.climb.api.mapper.DocumentoMapper;
import com.climb.api.model.Documento;
import com.climb.api.model.dto.DocumentoResponseDTO;
import com.climb.api.model.dto.DocumentoSolicitacaoRequestDTO;
import com.climb.api.model.dto.DocumentoValidacaoRequestDTO;
import com.climb.api.model.enums.DocumentoStatus;
import com.climb.api.repository.DocumentoRepository;
import com.climb.api.repository.EmpresaRepository;
import com.climb.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

        documento.setValidado(DocumentoStatus.PENDENTE);
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
        documento.setValidado(DocumentoStatus.EM_ANALISE);

        return documentoMapper.toResponseDto(documentoRepository.save(documento));
    }

    // Salvar o Arquivo
    // Nesse momento está salvando localmente
    // Depois deve ser integrado com o google drive
    private String salvarArquivo(MultipartFile arquivo) {
        try {
            validarArquivo(arquivo); // Cancela aqui se corrompido

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

    private void validarArquivo(MultipartFile arquivo) throws IOException {
        if (arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo está vazio.");
        }

        Tika tika = new Tika();
        String tipo = tika.detect(arquivo.getBytes());

        switch (tipo) {
            case "application/pdf" -> {
                try (PDDocument doc = Loader.loadPDF(arquivo.getBytes())) {
                    if (doc.getNumberOfPages() == 0)
                        throw new RuntimeException("PDF corrompido: sem páginas.");
                } catch (IOException e) {
                    throw new RuntimeException("PDF corrompido: " + e.getMessage());
                }
            }
            case "image/jpeg", "image/png", "image/gif", "image/bmp" -> {
                BufferedImage img = ImageIO.read(arquivo.getInputStream());
                if (img == null)
                    throw new RuntimeException("Imagem corrompida ou ilegível.");
            }
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                try (XSSFWorkbook wb = new XSSFWorkbook(arquivo.getInputStream())) {}
                catch (Exception e) {
                    throw new RuntimeException("XLSX corrompido: " + e.getMessage());
                }
            }
            case "application/vnd.ms-excel" -> {
                try (HSSFWorkbook wb = new HSSFWorkbook(arquivo.getInputStream())) {}
                catch (Exception e) {
                    throw new RuntimeException("XLS corrompido: " + e.getMessage());
                }
            }
            // Outros tipos são aceitos normalmente
        }

    }
}