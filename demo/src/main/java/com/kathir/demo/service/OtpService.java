package com.kathir.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail,String otp){

        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Otp for voter's portal registration");
        msg.setText("Your Otp "+otp+". It expires in 5 minutes");
        mailSender.send(msg);
    }

}
