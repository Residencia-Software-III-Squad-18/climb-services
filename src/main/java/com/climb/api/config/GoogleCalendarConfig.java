package com.climb.api.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Bean
    public GoogleClientSecrets googleClientSecrets() {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        GoogleClientSecrets secrets = new GoogleClientSecrets();
        secrets.setInstalled(details);
        return secrets;
    }
}