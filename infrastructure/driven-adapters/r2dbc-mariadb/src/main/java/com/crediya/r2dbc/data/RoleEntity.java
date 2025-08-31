package com.crediya.r2dbc.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("role")
public class RoleEntity implements Persistable<Long> {

    @Transient
    private boolean isNew = true; // default new

    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Override
    public Long getId() {
        return Long.valueOf(id);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
