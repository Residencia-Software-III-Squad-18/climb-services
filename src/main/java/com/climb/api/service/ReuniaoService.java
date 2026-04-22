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

    public Reuniao atualizar(Long id, Reuniao atualizada) {

        Reuniao reuniao = buscarPorId(id);

        reuniao.setTitulo(atualizada.getTitulo());
        reuniao.setEmpresa(atualizada.getEmpresa());
        reuniao.setData(atualizada.getData());
        reuniao.setHora(atualizada.getHora());
        reuniao.setPresencial(atualizada.getPresencial());
        reuniao.setLocal(atualizada.getLocal());
        reuniao.setPauta(atualizada.getPauta());
        reuniao.setStatus(atualizada.getStatus());

        return repository.save(reuniao);
    }

    public void deletar(Long id) {
        Reuniao reuniao = buscarPorId(id);
        repository.delete(reuniao);
    }
}
