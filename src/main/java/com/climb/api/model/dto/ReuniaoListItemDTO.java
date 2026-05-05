package com.climb.api.model.dto;

import com.climb.api.model.Reuniao;
import com.google.api.services.calendar.model.Event;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class ReuniaoListItemDTO {

    private Long idReuniao;
    private String titulo;
    private LocalDate data;
    private LocalTime hora;
    private Long empresaId;
    private EmpresaResumoDTO empresa;
    private String local;
    private Boolean presencial;
    private String pauta;
    private String status;
    private String googleEventId;

    private static final ZoneId ZONE = ZoneId.of("America/Fortaleza");

    public static ReuniaoListItemDTO fromEntity(Reuniao r) {
        ReuniaoListItemDTO d = new ReuniaoListItemDTO();
        d.setIdReuniao(r.getIdReuniao());
        d.setTitulo(r.getTitulo());
        d.setData(r.getData());
        d.setHora(r.getHora());
        if (r.getEmpresa() != null) {
            d.setEmpresaId(r.getEmpresa().getIdEmpresa());
            d.setEmpresa(EmpresaResumoDTO.from(r.getEmpresa()));
        }
        d.setLocal(r.getLocal());
        d.setPresencial(r.getPresencial());
        d.setPauta(r.getPauta());
        d.setStatus(r.getStatus());
        d.setGoogleEventId(r.getGoogleEventId());
        return d;
    }

    /**
     * Eventos criados só no Google Calendar (sem linha no Climb). ID sintético negativo para não colidir com PK do banco.
     */
    public static ReuniaoListItemDTO fromGoogleEventExterno(Event event) {
        ReuniaoListItemDTO d = new ReuniaoListItemDTO();
        d.setIdReuniao(syntheticId(event.getId()));
        d.setTitulo(event.getSummary() != null ? event.getSummary() : "(Sem título)");
        d.setGoogleEventId(event.getId());

        ZonedDateTime inicio = instantInicio(event);
        if (inicio != null) {
            d.setData(inicio.toLocalDate());
            d.setHora(inicio.toLocalTime().withNano(0));
        }

        d.setEmpresaId(0L);
        d.setEmpresa(null);
        d.setLocal(event.getLocation());
        boolean virtual =
                (event.getHangoutLink() != null && !event.getHangoutLink().isBlank())
                        || (event.getConferenceData() != null
                                && event.getConferenceData().getEntryPoints() != null
                                && !event.getConferenceData().getEntryPoints().isEmpty());
        d.setPresencial(!virtual);
        d.setPauta(event.getDescription());
        d.setStatus("GOOGLE_CALENDAR");
        return d;
    }

    private static long syntheticId(String googleEventId) {
        long h = (long) googleEventId.hashCode();
        if (h >= 0) {
            return -h - 1L;
        }
        return h;
    }

    private static ZonedDateTime instantInicio(Event event) {
        if (event.getStart() == null) {
            return null;
        }
        if (event.getStart().getDateTime() != null) {
            long ms = event.getStart().getDateTime().getValue();
            return ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(ms), ZONE);
        }
        if (event.getStart().getDate() != null) {
            String raw = event.getStart().getDate().toString();
            if (raw != null && raw.length() >= 10) {
                LocalDate day = LocalDate.parse(raw.substring(0, 10));
                return day.atStartOfDay(ZONE);
            }
        }
        return null;
    }
}
