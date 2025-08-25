package com.crediya.api;

import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.api.mapper.AuthenticationMapper;
import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.TechnicalException;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
    private final AuthenticationMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return  serverRequest.bodyToMono(RegisterUserRequest.class)
            .doOnNext(user -> log.info("Received registerUser request with user: {}", user))
            .flatMap(dto -> {
                var violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .reduce((a, b) -> a + "; " + b)
                            .orElse("Invalid request");
                    return ServerResponse.badRequest().bodyValue(errorMsg);
                }
                return authenticationUseCase.registerUser(mapper.toModel(dto))
                .doOnSuccess(saved -> log.info("User registered successfully: {}", saved))
                .doOnError(e -> log.error("Error while registering user", e))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(BusinessException.class, e ->ServerResponse.badRequest().bodyValue(e.getBusinessErrorMessage().toString()))
                .onErrorResume(TechnicalException.class,
                e -> ServerResponse.status(500).bodyValue(e.getTechnicalErrorMessage().toString()));
            });
    }
}
