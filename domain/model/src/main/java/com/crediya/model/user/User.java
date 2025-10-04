package com.crediya.model.user;
import com.crediya.model.role.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Integer idType;
    private Integer identificationNumber;
    private String firstNames;
    private String lastNames;
    private String email ;
    private String phone;
    private BigDecimal baseSalary;
    private LocalDate birthDate;
    private String address;
    private Integer roleId;
    private Role role;
    private String password;
}
