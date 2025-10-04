package com.crediya.r2dbc;

import com.crediya.r2dbc.data.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<UserEntity> findFirstByEmail(String email);

    Mono<UserEntity> findFirstByIdentificationNumber(Integer identificationNumber);

    Mono<UserEntity> findFirstByEmailAndPassword(String email, String password);

    @Query("SELECT u.email " +
            "FROM user u " +
            "WHERE u.roleId = 1")
    Flux<String> findAllAdminEmails();
}
