package com.crediya.model.permission;

import com.crediya.model.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Permission {
    private Long id;
    private String path;
    private String method;
    private Integer roleId;
    private String roleName;
    private String serverId;
}
