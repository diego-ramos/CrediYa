package com.crediya.r2dbc.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Table("user")
public class UserEntity implements Persistable<Long> {

    @Transient
    private boolean isNew = true; // default new

    @Id
    @Column("idNumber")
    private Integer identificationNumber;

    @Column("idType")
    private Integer idType;

    @Column("firstNames")
    private String firstNames;

    @Column("lastNames")
    private String lastNames;

    @Column("email")
    private String email ;

    @Column("phone")
    private String phone;

    @Column("baseSalary")
    private BigDecimal baseSalary;

    @Column("birthDate")
    private LocalDate birthDate;

    @Column("address")
    private String address;

    @Column("roleId")
    private Integer roleId;

    @Override
    public Long getId() {
        return Long.valueOf(identificationNumber);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
