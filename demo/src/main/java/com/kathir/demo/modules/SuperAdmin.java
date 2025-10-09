package com.kathir.demo.modules;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    @NotNull
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp="^[0-9]{10}$")
    private long phoneNumber;
    @NotBlank
    private String password;
}
