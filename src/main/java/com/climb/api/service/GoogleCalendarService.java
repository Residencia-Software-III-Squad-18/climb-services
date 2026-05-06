package com.climb.api.service;

import com.climb.api.model.Reuniao;
import com.climb.api.util.LogSanitizer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.api.client.util.DateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarService.class);

    public String criarEvento(Reuniao reuniao, String accessToken) throws Exception {
        Event created = buildCalendar(accessToken).events()
                .insert("primary", buildEvent(reuniao))
                .setConferenceDataVersion(1)
                .execute();

        return created.getId();
    }

    public void atualizarEvento(Reuniao reuniao, String accessToken) throws Exception {
        if (reuniao.getGoogleEventId() == null || reuniao.getGoogleEventId().isBlank()) {
            return;
        }

        buildCalendar(accessToken).events()
                .update("primary", reuniao.getGoogleEventId(), buildEvent(reuniao))
                .setConferenceDataVersion(1)
                .execute();
    }

    public boolean eventoExiste(String googleEventId, String accessToken) throws Exception {
        if (googleEventId == null || googleEventId.isBlank()) {
            return true;
        }

        try {
            buildCalendar(accessToken).events().get("primary", googleEventId).execute();
            return true;
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404 || e.getStatusCode() == 410) {
                return false;
            }
            throw e;
        }
    }

    private Calendar buildCalendar(String accessToken) throws Exception {
        GoogleCredentials credentials = GoogleCredentials
                .create(new AccessToken(accessToken, null))
                .createScoped(List.of(CalendarScopes.CALENDAR));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Climbe")
                .build();
    }

    private Event buildEvent(Reuniao reuniao) {
        Event event = new Event()
                .setSummary(reuniao.getTitulo())
                .setDescription(reuniao.getPauta())
                .setLocation(reuniao.getLocal());

        ZoneId zoneId = ZoneId.of("America/Fortaleza");
        ZonedDateTime inicio = ZonedDateTime.of(reuniao.getData(), reuniao.getHora(), zoneId);
        ZonedDateTime fim = inicio.plusHours(1);
        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(inicio.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                .setTimeZone("America/Fortaleza");
        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(fim.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                .setTimeZone("America/Fortaleza");
        event.setStart(start);
        event.setEnd(end);

        if (Boolean.FALSE.equals(reuniao.getPresencial())) {
            ConferenceSolutionKey key = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest req = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(key);
            event.setConferenceData(new ConferenceData().setCreateRequest(req));
        }

        return event;
    }

    public List<Event> listarEventosPrimarios(String accessToken, Instant timeMin, Instant timeMax) throws Exception {
        int tokenLen = accessToken != null ? accessToken.length() : 0;
        log.info("GoogleCalendar list(primary) — tokenLen={}, janela {} .. {}", tokenLen, timeMin, timeMax);
        Calendar.Events.List request = buildCalendar(accessToken).events().list("primary");
        request.setTimeMin(new DateTime(timeMin.toEpochMilli()));
        request.setTimeMax(new DateTime(timeMax.toEpochMilli()));
        request.setSingleEvents(true);
        request.setOrderBy("startTime");
        Events events = request.execute();
        List<Event> items = events.getItems() != null ? events.getItems() : List.of();
        List<Event> filtrados = items.stream()
                .filter(GoogleCalendarService::incluirNaMesclaComClimb)
                .toList();
        log.info("GoogleCalendar list(primary) — api={}, após incluirNaMesclaComClimb={}", items.size(), filtrados.size());
        return filtrados;
    }

    /**
     * Inclui na mescla com o Climb tudo que tem início válido, exceto tipos sintéticos do Google que
     * costumam poluir a agenda como "00:00 Home" ({@code workingLocation}) ou blocos de foco.
     * <p>
     * Não filtramos {@code birthday}, {@code outOfOffice}, {@code fromGmail} nem {@code default} —
     * filtrar isso removeu eventos reais em alguns tenants.
     */
    private static boolean incluirNaMesclaComClimb(Event ev) {
        if (ev == null) {
            return false;
        }
        EventDateTime start = ev.getStart();
        if (start == null || (start.getDateTime() == null && start.getDate() == null)) {
            return false;
        }
        String type = ev.getEventType();
        if (type == null || type.isBlank()) {
            return true;
        }
        return switch (type.trim().toLowerCase(Locale.ROOT)) {
            case "workinglocation", "focustime" -> false;
            default -> true;
        };
    }

    public void deletarEvento(String googleEventId, String accessToken) throws Exception {
        try {
            buildCalendar(accessToken).events().delete("primary", googleEventId).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() != 404 && e.getStatusCode() != 410) {
                throw e;
            }
        }
    }
}
