package com.crediya.model.user;
import com.crediya.model.role.Role;
import lombok.*;

import java.time.LocalDate;

@Data
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
    private Role role;

}
