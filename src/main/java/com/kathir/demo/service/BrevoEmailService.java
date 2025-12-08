package com.kathir.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrevoEmailService {


    private final WebClient brevoClient;

    @Value("${brevo.sender.email}")
    private  String senderEmail;
    @Value("${brevo.sender.name}")
    private  String senderName;



    public Mono<Void> sendOtpEmail(String toEmail, String otp) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("email", senderEmail, "name", senderName),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "Your OTP Code",
                "htmlContent", "<p>Your OTP is <strong>" + otp + "</strong>. It expires in 15 minutes.</p>"
        );

        return brevoClient.post()
                .uri("/smtp/email")
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}

