package com.crediya.api;

import com.crediya.api.dto.RegisterUserRequest;
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
}
