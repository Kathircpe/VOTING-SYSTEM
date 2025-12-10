package com.kathir.demo.service;

import com.kathir.demo.configuration.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailConfig emailConfig;

    private static final String SUBJECT = "otp for Voting Portal";

    public void sendOtpEmail(String toEmail, String otp) {

        final String body = "Your otp for Voting portal " + otp + ". It expires in 15 minutes and do not share it with anyone";

        try{
            MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties()));
            email.setFrom(new InternetAddress("me"));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            email.setSubject(SUBJECT);
            email.setText(body);

            // Encode the email
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray());

            // Create and send the message via Gmail API
            Message message = new Message();
            message.setRaw(encodedEmail);

            emailConfig.getGmailService().users().messages().send("me", message).execute();
            System.out.println("email has been sent to "+toEmail);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}

