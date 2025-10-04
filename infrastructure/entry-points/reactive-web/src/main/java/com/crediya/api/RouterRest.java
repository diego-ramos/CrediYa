package com.crediya.api;

import com.crediya.api.dto.LoginRequest;
import com.crediya.api.dto.RegisterUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios/register",
                    produces = { MediaType.APPLICATION_JSON_VALUE },
                    method = RequestMethod.POST,
                    beanClass = AuthenticationHandlerV1.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Registrar un nuevo usuario",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "Datos del usuario a registrar",
                                    content = @Content(schema = @Schema(implementation = RegisterUserRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/login",
                    produces = { MediaType.APPLICATION_JSON_VALUE },
                    method = RequestMethod.POST,
                    beanClass = AuthenticationHandlerV1.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "User Login",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "Login user data",
                                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "User Authenticated"),
                                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(AuthenticationHandlerV1 authenticationHandlerV1) {
        return RouterFunctions
            .route()
                .path("/api/v1", builder -> builder
                        .POST("/usuarios/register", authenticationHandlerV1::registerUser)
                        .GET("/usuarios/identification-number/{identificationNumber}", authenticationHandlerV1::getUserByIdentificationNumber)
                        .POST("/usuarios/login",  authenticationHandlerV1::login)
                        .GET("/usuarios/get-admins-emails",  authenticationHandlerV1::findAllAdminEmails))
            .build();
        }
}
