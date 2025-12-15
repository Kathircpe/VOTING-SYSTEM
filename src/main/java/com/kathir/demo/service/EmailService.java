package com.kathir.demo.service;

import com.kathir.demo.configuration.EmailConfig;
import lombok.RequiredArgsConstructor;
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

        final String body =
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "  <meta charset='UTF-8'>" +
            "</head>" +
            "<body style='margin:0; padding:0; background-color:#f5f7fa; font-family:Arial, Helvetica, sans-serif;'>" +
            "  <table width='100%' cellpadding='0' cellspacing='0'>" +
            "    <tr>" +
            "      <td align='center' style='padding:40px 10px;'>" +
            "        <table width='360' cellpadding='0' cellspacing='0' style='background:#ffffff; border-radius:6px; padding:30px; text-align:center;'>" +
            "          <tr>" +
            "            <td style='font-size:16px; color:#333333;'>Your One-Time Password</td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td style='padding:20px 0;'>" +
            "              <div style='font-size:32px; letter-spacing:6px; font-weight:bold; color:#000000;'>" +
            "                " + otp + 
            "              </div>" +
            "            </td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td style='font-size:13px; color:#666666;'>This code is valid for 15 minnutes only.</td>" +
            "          </tr>" +
            "        </table>" +
            "      </td>" +
            "    </tr>" +
            "  </table>" +
            "</body>" +
            "</html>";


        try{
            MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties()));
            email.setFrom(new InternetAddress("me"));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            email.setSubject(SUBJECT);
            email.setContent(body, "text/html; charset=UTF-8");

            // Encoding the email
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

