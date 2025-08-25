package com.crediya.api.dto;

import com.crediya.model.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class UserDTO {

    private Integer idType;
    private Integer idNumber;
    private String firstNames;
    private String lastNames;
    private String email ;
    private String phone;
    private Long baseSalary;
    private LocalDate birthDate;
    private String address;
    private Role role;
}
