package com.crediya.api;

import com.crediya.api.dto.IdentificationNumberRequest;
import com.crediya.api.dto.LoginRequest;
import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.api.mapper.AuthenticationMapper;
import com.crediya.api.security.JwtService;
import com.crediya.model.user.AuthResponse;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@ContextConfiguration(classes = {RouterRest.class, AuthenticationHandlerV1.class, TestSecurityConfig.class})
@WebFluxTest
class RouterRestTest {

    private static final String ADMIN_ROLE = "ROLE_ADMINISTRADOR";
    private static final String CUSTOMER_ROLE = "ROLE_CLIENTE";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthenticationUseCase authenticationUseCase;

    @MockitoBean
    private AuthenticationMapper mapper;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void testRegisterUser() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darp@test.com",
                BigDecimal.valueOf(1_000_000),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        User user = new User();
        user.setEmail("test@test.com");


        Mockito.when(authenticationUseCase.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
                .uri("/api/v1/usuarios/register")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testRegisterUserBadRole() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darp@test.com",
                BigDecimal.valueOf(1_000_000),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        User user = new User();
        user.setEmail("test@test.com");


        Mockito.when(authenticationUseCase.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(CUSTOMER_ROLE))
                )
                .post()
                .uri("/api/v1/usuarios/register")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();

    }

    @Test
    void testRegisterUserBadSalary() {
        RegisterUserRequest request = new RegisterUserRequest(
                79948037,
                "Diego Alberto",
                "Ramos Patarroyo",
                "darp@test.com",
                BigDecimal.valueOf(1_000_000_000_000L),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        User user = new User();
        user.setEmail("test@test.com");
        user.setBaseSalary(BigDecimal.valueOf(1_000_000_000_000L));

        Mockito.when(authenticationUseCase.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
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
                BigDecimal.valueOf(1_000_000L),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
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
                BigDecimal.valueOf(1_000_000L),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
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
                BigDecimal.valueOf(1_000_000L),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
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
                BigDecimal.valueOf(1_000_000L),
                "3113562536",
                LocalDate.of(1978, 6, 22),
                "Calle 3 26 #33-45"
        );

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Identification number is required"));
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

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .post()
                .uri("/api/v1/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Base Salary is required"));
    }

    @Test
    void testGetUserByIdentificationNumber() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setBaseSalary(BigDecimal.valueOf(1000));

        Mockito.when(authenticationUseCase.getUserByIdentificationNumber(123))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .get()
                .uri("/api/v1/usuarios/identification-number/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }

    @Test
    void testGetUserByIdentificationNumberWrongIdType() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setBaseSalary(BigDecimal.valueOf(1000));

        Mockito.when(authenticationUseCase.getUserByIdentificationNumber(123))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(
                        mockJwt()
                                .authorities(new SimpleGrantedAuthority(ADMIN_ROLE))
                )
                .get()
                .uri("/api/v1/usuarios/identification-number/123a")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class);
    }

    @Test
    void testLogin() {

        LoginRequest loginRequest = new LoginRequest("darp@test.com", "123");

        AuthResponse authResponse = new AuthResponse("ad123", "darp@test.com", CUSTOMER_ROLE);

        Mockito.when(authenticationUseCase.login(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(authResponse));

        webTestClient
                .post()
                .uri("/api/v1/usuarios/login")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testLoginBadEmail() {

        LoginRequest loginRequest = new LoginRequest("darptest.com", "123");

        webTestClient
                .post()
                .uri("/api/v1/usuarios/login")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
