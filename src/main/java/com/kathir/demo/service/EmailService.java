package com.kathir.demo.service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {


    private final MailerSend mailerSend;

    @Value("${resend.sender.email}")
    private String senderEmail;
    @Value("${resend.sender.name}")
    private String senderName;


    public void sendOtpEmail(String toEmail, String otp) {

        Email email = new Email();
        email.setFrom(senderName, senderEmail);
        email.addRecipient("", toEmail);
        email.setSubject("otp for Voting Portal");
        email.setHtml("<p>Your otp for Voting portal <strong>" + otp + "</strong>. " +
                "<br>It expires in 15 minutes and do not share it with anyone </p>");

        try {
            MailerSendResponse res = mailerSend.emails().send(email);
            System.out.println(res.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }
}

