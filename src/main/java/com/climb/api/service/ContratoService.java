package com.climb.api.service;

import com.climb.api.model.Contrato;
import com.climb.api.repository.ContratoRepository;
import com.climb.api.repository.PropostaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class ContratoService {

    private final ContratoRepository repository;
    private final PropostaRepository propostaRepository;
    private final ContratoNotificacaoService contratoNotificacaoService;
    private final int diasAvisoVencimento;

    public ContratoService(ContratoRepository repository,
                           PropostaRepository propostaRepository,
                           ContratoNotificacaoService contratoNotificacaoService,
                           @Value("${app.contract-notifications.expiration-warning-days:30}") int diasAvisoVencimento) {
        this.repository = repository;
        this.propostaRepository = propostaRepository;
        this.contratoNotificacaoService = contratoNotificacaoService;
        this.diasAvisoVencimento = diasAvisoVencimento;
    }

    public List<Contrato> listar() {
        return repository.findAll();
    }

    public Contrato buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Contrato> listarPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public Contrato criar(Contrato contrato) {
        contrato.setProposta(propostaRepository.findById(obterPropostaId(contrato))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposta nao encontrada")));
        Contrato salvo = repository.save(contrato);
        contratoNotificacaoService.notificarContratoCriado(salvo);
        return salvo;
    }

    public Contrato atualizar(Long id, Contrato atualizado) {
        Contrato contrato = buscarPorId(id);
        Contrato anterior = snapshot(contrato);
        contrato.setDataInicio(atualizado.getDataInicio());
        contrato.setDataFim(atualizado.getDataFim());
        contrato.setStatus(atualizado.getStatus());
        Contrato salvo = repository.save(contrato);
        contratoNotificacaoService.notificarContratoAtualizado(anterior, salvo);
        return salvo;
    }

    public void deletar(Long id) {
        Contrato contrato = buscarPorId(id);
        contratoNotificacaoService.notificarContratoRemovido(contrato);
        repository.delete(contrato);
    }

    @Scheduled(cron = "${app.contract-notifications.expiration-cron:0 0 8 * * *}")
    public void notificarVencimentosProximos() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(diasAvisoVencimento);
        repository.findByDataFimBetween(hoje, limite)
                .stream()
                .filter(contrato -> !statusIgnoradoParaVencimento(contrato.getStatus()))
                .forEach(contrato -> contratoNotificacaoService.notificarVencimentoProximo(contrato, hoje));
    }

    private boolean statusIgnoradoParaVencimento(String status) {
        if (status == null) {
            return false;
        }

        return Set.of("ENCERRADO", "CANCELADO", "INATIVO").contains(status.toUpperCase());
    }

    private Contrato snapshot(Contrato contrato) {
        Contrato snapshot = new Contrato();
        snapshot.setIdContrato(contrato.getIdContrato());
        snapshot.setProposta(contrato.getProposta());
        snapshot.setDataInicio(contrato.getDataInicio());
        snapshot.setDataFim(contrato.getDataFim());
        snapshot.setStatus(contrato.getStatus());
        return snapshot;
    }

    private Long obterPropostaId(Contrato contrato) {
        if (contrato.getProposta() == null || contrato.getProposta().getIdProposta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proposta e obrigatoria");
        }

        return contrato.getProposta().getIdProposta();
    }
}
