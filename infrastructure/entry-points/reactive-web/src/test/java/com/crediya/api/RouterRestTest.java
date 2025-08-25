package com.crediya.api;

import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.api.mapper.AuthenticationMapper;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = {RouterRest.class, AuthenticationHandlerV1.class, AuthenticationHandlerV2.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthenticationUseCase authenticationUseCase;

    @MockitoBean
    private AuthenticationMapper mapper;

    @Test
    void testRegisterUser() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darp@test.com",
                1_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        User user = new User();
        user.setEmail("test@test.com");


        Mockito.when(authenticationUseCase.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testRegisterUserBadSalary() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darp@test.com",
                1_000_000_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        User user = new User();
        user.setEmail("test@test.com");
        user.setBaseSalary(1_000_000_000_000L);

        Mockito.when(authenticationUseCase.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testRegisterUserBadEmail() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darptest.com", // Invalid
                1_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Email must be valid"));
    }

    @Test
    void testRegisterUserEmptyName() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "",
                "Ramos Patarroyo",
                "darp@test.com",
                1_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Firsts names are required"));
    }

    @Test
    void testRegisterUserEmptyLastName() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "AAA",
                "",
                "darp@test.com",
                1_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Lasts names are required"));
    }
    @Test
    void testRegisterUserNullIdNumber() {
        RegisterUserRequest request = new RegisterUserRequest(
                null,
                "AAA",
                "",
                "darp@test.com",
                1_000_000L,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Id number is required"));
    }

    @Test
    void testRegisterUserNullBaseSalary() {
        RegisterUserRequest request = new RegisterUserRequest(
                123,
                "AAA",
                "",
                "darp@test.com",
                null,
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Base Salary is required"));
    }

}
