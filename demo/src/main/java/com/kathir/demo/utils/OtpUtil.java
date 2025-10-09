package com.kathir.demo.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpUtil {
    private final SecureRandom random = new SecureRandom();

    public String generateOtp() {
        StringBuilder sb = new StringBuilder();
        int length = 6;
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
