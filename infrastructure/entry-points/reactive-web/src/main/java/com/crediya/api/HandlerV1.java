package com.crediya.api;

import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.TechnicalException;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {
    private final AuthenticationUseCase authenticationUseCase;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return  serverRequest.bodyToMono(User.class)
                .doOnNext(user -> log.info("Received registerUser request with user: {}", user))
                .flatMap(authenticationUseCase::registerUser)
                .doOnSuccess(saved -> log.info("User registered successfully: {}", saved))
                .doOnError(e -> log.error("Error while registering user", e))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(TechnicalException.class,
                e -> ServerResponse.status(500).bodyValue(e.getTechnicalErrorMessage().toString()));
    }
}