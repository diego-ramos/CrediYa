package com.crediya.model.usuario;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer idType;
    private Integer idNumber;
    private String firstNames;
    private String lastNames;
    private String email ;
    private String phone;
    private Long baseSalary;
    private LocalDate birthDate;
    private String address;
    private int rolId;

}
