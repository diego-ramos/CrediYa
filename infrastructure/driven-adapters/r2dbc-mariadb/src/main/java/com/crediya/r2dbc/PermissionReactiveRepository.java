package com.crediya.r2dbc;

import com.crediya.r2dbc.data.PermissionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PermissionReactiveRepository extends ReactiveCrudRepository<PermissionEntity, String>, ReactiveQueryByExampleExecutor<PermissionEntity> {
    @Query("SELECT p.id, p.path, p.method, r.name AS role_name, p.server_id " +
            "FROM permission p " +
            "JOIN role r ON p.role_id = r.id " +
            "WHERE p.server_id = :serverId")
    Flux<PermissionEntity> findAllByServerId(String serverId);

    @Query("SELECT p.id, p.path, p.method, r.name AS role_name, p.server_id " +
            "FROM permission p " +
            "JOIN role r ON p.role_id = r.id " +
            "WHERE p.role_id = :roleId")
    Flux<PermissionEntity> findAllByRoleId(Integer roleId);
}
