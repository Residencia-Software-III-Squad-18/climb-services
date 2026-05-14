package com.climb.api.service;

import com.climb.api.model.Contrato;
import com.climb.api.model.Empresa;
import com.climb.api.model.Relatorio;
import com.climb.api.model.dto.RelatorioPdfDownloadDTO;
import com.climb.api.repository.ContratoRepository;
import com.climb.api.repository.RelatorioRepository;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RelatorioService {

    private static final DateTimeFormatter DATA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
    private static final String TEMPLATE_PATH = "reports/templates/relatorio-template.jrxml";
    private static final String LOGO_PATH = "reports/assets/climb-logo.jpeg";

    private final RelatorioRepository repository;
    private final ContratoRepository contratoRepository;
    private final Path pastaRelatorios;

    public RelatorioService(RelatorioRepository repository,
                            ContratoRepository contratoRepository,
                            @Value("${app.reports.output-dir:uploads/relatorios}") String pastaRelatorios) {
        this.repository = repository;
        this.contratoRepository = contratoRepository;
        this.pastaRelatorios = Paths.get(pastaRelatorios);
    }

    public Relatorio uploadPdf(Long id, MultipartFile file) {
        Relatorio relatorio = buscarPorId(id);

        byte[] conteudo = validarPdf(file);

        Path caminho = resolverCaminhoPdf(relatorio.getIdRelatorio());

        try {
            Files.createDirectories(caminho.getParent());

            Files.write(
                    caminho,
                    conteudo,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nao foi possivel salvar o PDF do relatorio"
            );
        }

        relatorio.setUrlPdf(caminho.toString());

        if (relatorio.getDataEnvio() == null) {
            relatorio.setDataEnvio(LocalDate.now());
        }

        return repository.save(relatorio);
    }

    private byte[] validarPdf(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arquivo PDF e obrigatorio"
            );
        }

        try {
            byte[] conteudo = arquivo.getBytes();

            Tika tika = new Tika();
            String tipo = tika.detect(conteudo);

            if (!MediaType.APPLICATION_PDF_VALUE.equals(tipo)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Arquivo deve ser um PDF"
                );
            }

            try (PDDocument doc = Loader.loadPDF(conteudo)) {
                if (doc.getNumberOfPages() == 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "PDF invalido ou corrompido"
                    );
                }
            }

            return conteudo;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PDF invalido ou corrompido"
            );
        }
    }

    public List<Relatorio> listar() {
        return repository.findAll();
    }

    public List<Relatorio> listarPorContrato(Long contratoId) {
        return repository.findByContrato_IdContrato(contratoId);
    }

    public Relatorio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Relatorio nao encontrado"));
    }

    public Relatorio criar(Relatorio relatorio) {
        Relatorio novoRelatorio = new Relatorio();
        novoRelatorio.setContrato(buscarContrato(relatorio));
        novoRelatorio.setDescricao(relatorio.getDescricao());
        novoRelatorio.setDataEnvio(relatorio.getDataEnvio());
        return repository.save(novoRelatorio);
    }

    public Relatorio atualizar(Long id, Relatorio atualizado) {
        Relatorio relatorio = buscarPorId(id);
        if (atualizado.getContrato() != null && atualizado.getContrato().getIdContrato() != null) {
            relatorio.setContrato(buscarContrato(atualizado));
        }
        if (atualizado.getDescricao() != null) {
            relatorio.setDescricao(atualizado.getDescricao());
        }
        if (atualizado.getDataEnvio() != null) {
            relatorio.setDataEnvio(atualizado.getDataEnvio());
        }
        apagarPdfExistente(relatorio.getUrlPdf());
        relatorio.setUrlPdf(null);
        return repository.save(relatorio);
    }

    public void deletar(Long id) {
        Relatorio relatorio = buscarPorId(id);
        apagarPdfExistente(relatorio.getUrlPdf());
        repository.delete(relatorio);
    }

    public Relatorio exportarPdf(Long id) {
        Relatorio relatorio = buscarPorId(id);
        byte[] pdf = gerarPdf(relatorio);
        Path caminho = resolverCaminhoPdf(relatorio.getIdRelatorio());

        try {
            Files.createDirectories(caminho.getParent());
            Files.write(caminho, pdf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel salvar o PDF do relatorio");
        }

        relatorio.setUrlPdf(caminho.toString());
        if (relatorio.getDataEnvio() == null) {
            relatorio.setDataEnvio(LocalDate.now());
        }

        return repository.save(relatorio);
    }

    public RelatorioPdfDownloadDTO obterPdfInline(Long id) {
        Relatorio relatorio = garantirPdfGerado(id);
        return new RelatorioPdfDownloadDTO(
                gerarNomeArquivo(relatorio, false),
                lerArquivoPdf(relatorio.getUrlPdf())
        );
    }

    public RelatorioPdfDownloadDTO obterPdfParaDownload(Long id) {
        Relatorio relatorio = garantirPdfGerado(id);
        return new RelatorioPdfDownloadDTO(
                gerarNomeArquivo(relatorio, true),
                lerArquivoPdf(relatorio.getUrlPdf())
        );
    }

    private Relatorio garantirPdfGerado(Long id) {
        Relatorio relatorio = buscarPorId(id);

        if (!StringUtils.hasText(relatorio.getUrlPdf()) || !Files.exists(Paths.get(relatorio.getUrlPdf()))) {
            relatorio = exportarPdf(id);
        }

        return relatorio;
    }

    private byte[] gerarPdf(Relatorio relatorio) {
        validarDadosParaExportacao(relatorio);

        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             InputStream logoStream = new ClassPathResource(LOGO_PATH).getInputStream()) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("LOGO_STREAM", logoStream);
            parametros.put("EMPRESA_NOME", obterEmpresa(relatorio).getRazaoSocial());
            parametros.put("DATA_RELATORIO", obterDataRelatorio(relatorio).format(DATA_FORMATTER));
            parametros.put("DESCRICAO", relatorio.getDescricao().trim());

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(templateStream),
                    parametros,
                    new JREmptyDataSource(1)
            );

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel carregar os recursos do relatorio");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel gerar o PDF do relatorio");
        }
    }

    private void validarDadosParaExportacao(Relatorio relatorio) {
        if (!StringUtils.hasText(relatorio.getDescricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descricao do relatorio e obrigatoria para exportacao");
        }

        obterEmpresa(relatorio);
    }

    private Empresa obterEmpresa(Relatorio relatorio) {
        Contrato contrato = relatorio.getContrato();
        if (contrato == null || contrato.getProposta() == null || contrato.getProposta().getEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relatorio sem empresa contratante vinculada");
        }

        return contrato.getProposta().getEmpresa();
    }

    private LocalDate obterDataRelatorio(Relatorio relatorio) {
        return relatorio.getDataEnvio() != null ? relatorio.getDataEnvio() : LocalDate.now();
    }

    private Contrato buscarContrato(Relatorio relatorio) {
        if (relatorio.getContrato() == null || relatorio.getContrato().getIdContrato() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contrato e obrigatorio");
        }

        return contratoRepository.findById(relatorio.getContrato().getIdContrato())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato nao encontrado"));
    }

    private byte[] lerArquivoPdf(String caminhoPdf) {
        try {
            return Files.readAllBytes(Paths.get(caminhoPdf));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel carregar o PDF do relatorio");
        }
    }

    private Path resolverCaminhoPdf(Long relatorioId) {
        return pastaRelatorios.resolve("relatorio-" + relatorioId + ".pdf");
    }

    private String gerarNomeArquivo(Relatorio relatorio, boolean paraDownload) {
        String nomeEmpresa = obterEmpresa(relatorio).getNomeFantasia();
        String base = StringUtils.hasText(nomeEmpresa) ? nomeEmpresa : obterEmpresa(relatorio).getRazaoSocial();
        String normalizado = base == null ? "relatorio" : base
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (!StringUtils.hasText(normalizado)) {
            normalizado = "relatorio";
        }

        String prefixo = paraDownload ? "relatorio-" : "preview-relatorio-";
        return prefixo + normalizado + "-" + relatorio.getIdRelatorio() + ".pdf";
    }

    private void apagarPdfExistente(String caminhoPdf) {
        if (!StringUtils.hasText(caminhoPdf)) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(caminhoPdf));
        } catch (IOException ignored) {
            // O relatorio e removido mesmo se a limpeza do arquivo falhar.
        }
    }
}
