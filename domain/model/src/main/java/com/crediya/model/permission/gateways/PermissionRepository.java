package com.crediya.model.permission.gateways;

import com.crediya.model.permission.Permission;
import reactor.core.publisher.Flux;

public interface PermissionRepository {
    Flux<Permission> findByServerId(String serverId);
    Flux<Permission> findByRoleId(Integer roleId);
}
