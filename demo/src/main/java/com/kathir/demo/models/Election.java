package com.kathir.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Election {

    @Id
    private  Integer Id;
    @NotNull
    @NotBlank
    private  String electionName;

    @DateTimeFormat
    private LocalDateTime startDate;

    @DateTimeFormat
    private LocalDateTime endDate;


    private  String contractAddress;

}
