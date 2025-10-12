package com.kathir.demo.modules;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class Election {

    @Id
    private final int Id;
    @NotNull
    @NotBlank
    private final String election;
    @DateTimeFormat
    private final Date date;
    private final String contractAddress;

}
