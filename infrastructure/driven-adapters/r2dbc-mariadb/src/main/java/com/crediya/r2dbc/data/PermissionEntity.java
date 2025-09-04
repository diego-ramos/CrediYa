package com.crediya.r2dbc.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("permission")
public class PermissionEntity implements Persistable<Long> {

    @Transient
    private boolean isNew = true; // default new

    @Id
    @Column("id")
    private Long id;

    @Column("path")
    private String path;

    @Column("method")
    private String method;

    @Column("role_id")
    private Integer roleId;

    @Column("role_name")
    private String roleName;

    @Column("server_id")
    private String serverId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
