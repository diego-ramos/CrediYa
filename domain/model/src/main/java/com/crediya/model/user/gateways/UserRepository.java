package com.crediya.model.user.gateways;

import com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);

    Mono<User> findByEmail(String email);

    Mono<User> findByIdentificationNumber(Integer idNumber);

    Mono<User> findByEmailAndPassword(String email, String password);

    Flux<String> findAllAdminEmails();
}
