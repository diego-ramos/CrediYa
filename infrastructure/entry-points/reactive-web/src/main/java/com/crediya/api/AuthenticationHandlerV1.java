package com.crediya.api;

import com.crediya.api.dto.LoginRequest;
import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.api.dto.IdentificationNumberRequest;
import com.crediya.api.mapper.AuthenticationMapper;
import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.TechnicalException;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints related with user authentication")
public class AuthenticationHandlerV1 {
    private final AuthenticationUseCase authenticationUseCase;
    private final AuthenticationMapper mapper;
    private final Validator validator;

    @Operation(summary = "Register a new user", description = "Creates a new user in the system",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            )
    })
    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return  serverRequest.bodyToMono(RegisterUserRequest.class)
            .doOnNext(user -> log.info(Constants.USER_REGISTRATION_REQUEST_RECEIVED, user))
            .flatMap(dto -> {
                var violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .reduce((a, b) -> a + "; " + b)
                            .orElse(Constants.INVALID_REQUEST);
                    return ServerResponse.badRequest().bodyValue(errorMsg);
                }
                return authenticationUseCase.registerUser(mapper.toModel(dto))
                .doOnSuccess(saved -> log.info(Constants.USER_REGISTER_SUCCESS, saved))
                .doOnError(e -> log.error(Constants.ERROR_REGISTERING_USER, e))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(BusinessException.class, e ->ServerResponse.badRequest().bodyValue(e.getBusinessErrorMessage().toString()))
                .onErrorResume(TechnicalException.class,
                e -> ServerResponse.status(500).bodyValue(e.getTechnicalErrorMessage().toString()));
            });
    }

    public Mono<ServerResponse> getUserByIdentificationNumber(ServerRequest serverRequest) {
        int identificationNumber;

        try {
            identificationNumber = Integer.parseInt(serverRequest.pathVariable("identificationNumber"));
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest()
                    .bodyValue(Constants.IDENTIFICATION_NUMBER_MUST_BE_INTEGER);
        }

        return Mono.just(identificationNumber)
                .doOnNext(idNumber -> log.info(Constants.USER_SEARCH_REQUEST_RECEIVED, idNumber))
                .flatMap(idNumber -> authenticationUseCase.getUserByIdentificationNumber(idNumber)
                        .doOnSuccess(user -> log.info(Constants.USER_SEARCH_SUCCESS, user))
                        .doOnError(e -> log.error(Constants.ERROR_SEARCHING_USER, e))
                        // If user found -> return 200
                        .flatMap(user -> ServerResponse.ok().bodyValue(user))
                        // If no user found -> return 404
                        .switchIfEmpty(ServerResponse.status(404)
                                .bodyValue(Constants.USER_NOT_FOUND+ idNumber))
                        // Handle business error
                        .onErrorResume(BusinessException.class,
                                e -> ServerResponse.badRequest().bodyValue(e.getBusinessErrorMessage().toString()))
                        // Handle technical error
                        .onErrorResume(TechnicalException.class,
                                e -> ServerResponse.status(500).bodyValue(e.getTechnicalErrorMessage().toString()))
                );
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return  serverRequest.bodyToMono(LoginRequest.class)
                .doOnNext(login -> log.info(Constants.LOGIN_REQUEST_RECEIVED, login.email()))
                .flatMap(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errorMsg = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .reduce((a, b) -> a + "; " + b)
                                .orElse(Constants.INVALID_REQUEST);
                        return ServerResponse.badRequest().bodyValue(errorMsg);
                    }
                    return authenticationUseCase.login(dto.email(), dto.password())
                            .doOnSuccess(user -> log.info(Constants.LOGIN_SUCCESSFULLY, user))
                            .doOnError(e -> log.error(Constants.ERROR_LOGIN_USER, e))
                            .flatMap(auth -> ServerResponse.ok().bodyValue(auth))
                            .switchIfEmpty(ServerResponse.status(404)
                                    .bodyValue(Constants.USER_NOT_FOUND+ dto.email()))
                            .onErrorResume(BusinessException.class, e ->ServerResponse.badRequest().bodyValue(e.getBusinessErrorMessage().toString()))
                            .onErrorResume(TechnicalException.class,
                                    e -> ServerResponse.status(500).bodyValue(e.getTechnicalErrorMessage().toString()));
                });
    }
}
