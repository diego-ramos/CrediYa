package com.crediya.r2dbc;

import com.crediya.r2dbc.data.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, String>, ReactiveQueryByExampleExecutor<RoleEntity> {
    Mono<RoleEntity> findFirstById(Integer id);
}
