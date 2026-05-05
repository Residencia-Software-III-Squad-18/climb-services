package com.climb.api.service;

import com.climb.api.model.Reuniao;
import com.climb.api.repository.ReuniaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReuniaoService {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoService.class);

    private final ReuniaoRepository repository;
    private final GoogleCalendarService googleCalendarService;

    public ReuniaoService(ReuniaoRepository repository, GoogleCalendarService googleCalendarService) {
        this.repository = repository;
        this.googleCalendarService = googleCalendarService;
    }

    public List<Reuniao> listar() {
        return repository.findAll();
    }

    public List<Reuniao> listar(String googleAccessToken) {
        List<Reuniao> reunioes = repository.findAll();

        if (googleAccessToken == null || googleAccessToken.isBlank()) {
            return reunioes;
        }

        return reunioes.stream()
                .filter(reuniao -> sincronizarEventoGoogle(reuniao, googleAccessToken))
                .toList();
    }

    public Reuniao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reunião não encontrada"));
    }

    public List<Reuniao> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresa_IdEmpresa(empresaId);
    }

    public Reuniao criar(Reuniao reuniao, String accessToken) throws Exception {

        if (reuniao.getTitulo() == null || reuniao.getTitulo().isEmpty()) {
            throw new RuntimeException("Título é obrigatório");
        }

        if (reuniao.getEmpresa() == null) {
            throw new RuntimeException("Empresa é obrigatória");
        }

        Reuniao salva = repository.save(reuniao);

        if (accessToken == null || accessToken.isBlank()) {
            log.info("Reuniao {} criada sem integracao com Google Calendar por ausencia de token", salva.getIdReuniao());
            return salva;
        }

        try {
            String googleEventId = googleCalendarService.criarEvento(salva, accessToken);
            salva.setGoogleEventId(googleEventId);
            repository.save(salva);
        } catch (Exception e) {
            log.warn("Falha ao criar evento no Google Calendar para reunião {}: {}", salva.getIdReuniao(), e.getMessage());
        }

        return salva;
    }

    public Reuniao atualizar(Long id, Reuniao atualizada, String accessToken) {

        Reuniao reuniao = buscarPorId(id);

        reuniao.setTitulo(atualizada.getTitulo());
        reuniao.setEmpresa(atualizada.getEmpresa());
        reuniao.setData(atualizada.getData());
        reuniao.setHora(atualizada.getHora());
        reuniao.setPresencial(atualizada.getPresencial());
        reuniao.setLocal(atualizada.getLocal());
        reuniao.setPauta(atualizada.getPauta());
        reuniao.setStatus(atualizada.getStatus());

        Reuniao salva = repository.save(reuniao);

        if (accessToken != null && !accessToken.isBlank() && salva.getGoogleEventId() != null) {
            try {
                googleCalendarService.atualizarEvento(salva, accessToken);
            } catch (Exception e) {
                log.warn("Falha ao atualizar evento no Google Calendar para reunião {}: {}", salva.getIdReuniao(), e.getMessage());
            }
        }

        return salva;
    }

    public void deletar(Long id, String accessToken) {
        Reuniao reuniao = buscarPorId(id);
        if (
                accessToken != null &&
                !accessToken.isBlank() &&
                reuniao.getGoogleEventId() != null &&
                !reuniao.getGoogleEventId().isBlank()
        ) {
            try {
                googleCalendarService.deletarEvento(reuniao.getGoogleEventId(), accessToken);
            } catch (Exception e) {
                log.warn("Falha ao excluir evento no Google Calendar para reunião {}: {}", reuniao.getIdReuniao(), e.getMessage());
            }
        }
        repository.delete(reuniao);
    }

    private boolean sincronizarEventoGoogle(Reuniao reuniao, String accessToken) {
        if (reuniao.getGoogleEventId() == null || reuniao.getGoogleEventId().isBlank()) {
            return true;
        }

        try {
            if (googleCalendarService.eventoExiste(reuniao.getGoogleEventId(), accessToken)) {
                return true;
            }

            repository.delete(reuniao);
            log.info("Reuniao {} removida localmente porque o evento Google foi excluido", reuniao.getIdReuniao());
            return false;
        } catch (Exception e) {
            log.warn("Falha ao sincronizar evento Google da reunião {}: {}", reuniao.getIdReuniao(), e.getMessage());
            return true;
        }
    }
}
