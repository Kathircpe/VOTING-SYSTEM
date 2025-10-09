package com.kathir.demo.modules;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @NotBlank
    private String name;

    private Integer electorsRollId;

    private boolean hasVoted;

    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp="^[0-9]{10}$")
    private long phoneNumber;

    private String password;

    private String otp;

    private LocalDateTime otpExpiry;

}
