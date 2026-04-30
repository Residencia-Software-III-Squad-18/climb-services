package com.climb.api.service;

import com.climb.api.model.Reuniao;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final String TIME_ZONE = "America/Fortaleza";
    private static final ZoneId ZONE_ID = ZoneId.of(TIME_ZONE);

    public String criarEvento(Reuniao reuniao, String accessToken) throws Exception {

        GoogleCredentials credentials = GoogleCredentials
                .create(new AccessToken(accessToken, null))
                .createScoped(List.of(CalendarScopes.CALENDAR));

        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Climbe")
                .build();

        Event event = new Event()
                .setSummary(reuniao.getTitulo())
                .setDescription(reuniao.getPauta())
                .setLocation(reuniao.getLocal());

        LocalDateTime inicio = LocalDateTime.of(reuniao.getData(), reuniao.getHora());
        LocalDateTime fim = inicio.plusHours(1);

        EventDateTime startDateTime = new EventDateTime()
                .setDateTime(toGoogleDateTime(inicio))
                .setTimeZone(TIME_ZONE);
        EventDateTime endDateTime = new EventDateTime()
                .setDateTime(toGoogleDateTime(fim))
                .setTimeZone(TIME_ZONE);

        event.setStart(startDateTime);
        event.setEnd(endDateTime);

        if (Boolean.FALSE.equals(reuniao.getPresencial())) {
            ConferenceSolutionKey key = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest req = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(key);
            event.setConferenceData(new ConferenceData().setCreateRequest(req));
        }

        Event created = service.events()
                .insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        return created.getId();
    }

    private DateTime toGoogleDateTime(LocalDateTime localDateTime) {
        String value = localDateTime
                .atZone(ZONE_ID)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new DateTime(value);
    }

    public void deletarEvento(String googleEventId, String accessToken) throws Exception {

        GoogleCredentials credentials = GoogleCredentials
                .create(new AccessToken(accessToken, null))
                .createScoped(List.of(CalendarScopes.CALENDAR));

        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Climbe")
                .build();

        service.events().delete("primary", googleEventId).execute();
    }
}
