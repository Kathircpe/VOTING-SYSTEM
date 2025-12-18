package com.kathir.demo.configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {


    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.refresh.token}")
    private String refreshToken;

    private Gmail gmailService;

    @PostConstruct
    public void init() throws Exception {
        getGmailService();
    }
    // This method creates the Gmail service with fresh credentials
    public synchronized Gmail getGmailService() throws Exception {
        if (gmailService == null) {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Build the credential using the refresh token
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setRefreshToken(refreshToken);

            // This automatically gets a new access token
            credential.refreshToken();

            gmailService = new Gmail.Builder(httpTransport,
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("voting-portal")
                    .build();
        }
        return gmailService;
    }

}

