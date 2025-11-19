package com.kathir.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    private String name;

    private Integer age;

    private boolean hasVoted;

    private boolean isEnabled;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String phoneNumber;
    @NotNull
    @NotBlank
    private String voterAddress; // Blockchain wallet address

    private String password;

    private String otp;


    private LocalDateTime expiration;
}
