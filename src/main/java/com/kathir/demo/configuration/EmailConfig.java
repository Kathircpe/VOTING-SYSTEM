package com.kathir.demo.configuration;

import com.mailersend.sdk.MailerSend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${resend.base-url}")
    private String baseUrl;
    @Value("${resend.api-key}")
    private String apiKey;


    @Bean
    public MailerSend resendClient() {
        MailerSend ms=new MailerSend();
        ms.setToken(apiKey);
        return ms;
    }
}

