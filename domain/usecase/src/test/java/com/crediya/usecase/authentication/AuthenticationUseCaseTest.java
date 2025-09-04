package com.crediya.usecase.authentication;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.exception.message.TechnicalErrorMessage;
import com.crediya.model.jwtprovider.JwtProvider;
import com.crediya.model.role.Role;
import com.crediya.model.role.gateways.RoleRepository;
import com.crediya.model.user.AuthResponse;
import com.crediya.model.user.User;
import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.message.BusinessErrorMessage;
import com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private final User validUser = User.builder()
            .email("test@mail.com")
            .baseSalary(BigDecimal.valueOf(1_000_000L))
            .build();

    @Test
    void mustRegisterUserSuccessfully() {

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.findByIdentificationNumber(validUser.getIdentificationNumber())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        Mono<User> result = authenticationUseCase.registerUser(validUser);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getBaseSalary().compareTo(BigDecimal.valueOf(1_000_000L)) == 0)
                .verifyComplete();

        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    void mustFailWhenSalaryIsNegative() {
        User user = new User();
        user.setBaseSalary(BigDecimal.valueOf(-1000L));

        Mono<User> result = authenticationUseCase.registerUser(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    BusinessException ex = (BusinessException) error;
                    assert ex.getBusinessErrorMessage().equals(BusinessErrorMessage.SALARY_OUT_OF_RANGE);
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void mustFailWhenSalaryIsAboveLimit() {
        User user = new User();
        user.setBaseSalary(BigDecimal.valueOf(20_000_000L));

        Mono<User> result = authenticationUseCase.registerUser(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    BusinessException ex = (BusinessException) error;
                    assert ex.getBusinessErrorMessage().equals(BusinessErrorMessage.SALARY_OUT_OF_RANGE);
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void mustFailWhenEmailAlreadyRegistered() {
        // arrange
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.just(validUser));
        when(userRepository.findByIdentificationNumber(validUser.getIdentificationNumber())).thenReturn(Mono.empty());

        // act & assert
        StepVerifier.create(authenticationUseCase.registerUser(validUser))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    BusinessException ex = (BusinessException) error;
                    assert ex.getBusinessErrorMessage().equals(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED);
                })
                .verify();
    }

    @Test
    void mustFailWhenIdNumberAlreadyRegistered() {
        User userWithId = validUser.toBuilder().identificationNumber(123456789).build();

        when(userRepository.findByEmail(userWithId.getEmail()))
                .thenReturn(Mono.empty());
        when(userRepository.findByIdentificationNumber(userWithId.getIdentificationNumber()))
                .thenReturn(Mono.just(userWithId));

        StepVerifier.create(authenticationUseCase.registerUser(userWithId))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    BusinessException ex = (BusinessException) error;
                    assert ex.getBusinessErrorMessage().equals(BusinessErrorMessage.IDENTIFICATION_NUMBER_ALREADY_REGISTERED);
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void mustRegisterUserWhenEmailAndIdNumberAreFree() {
        User userWithId = validUser.toBuilder().identificationNumber(987654321).build();

        when(userRepository.findByEmail(userWithId.getEmail())).thenReturn(Mono.empty());
        when(userRepository.findByIdentificationNumber(userWithId.getIdentificationNumber())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithId));

        StepVerifier.create(authenticationUseCase.registerUser(userWithId))
                .expectNextMatches(saved ->
                        saved.getBaseSalary().compareTo(BigDecimal.valueOf(1_000_000L)) == 0 &&
                                saved.getIdentificationNumber().equals(987654321))
                .verifyComplete();

        verify(userRepository, times(1)).save(userWithId);
    }

    @Test
    void mustGetUserByIdentificationNumber() {

        User userWithId = validUser.toBuilder().identificationNumber(123).build(); // match the test input

        when(userRepository.findByIdentificationNumber(123))
                .thenReturn(Mono.just(userWithId));

        Mono<User> result = authenticationUseCase.getUserByIdentificationNumber(123);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getBaseSalary().compareTo(BigDecimal.valueOf(1_000_000L)) == 0)
                .verifyComplete();

        verify(userRepository, times(1)).findByIdentificationNumber(123);
    }

    @Test
    void mustLogin() {
        User userWithId = validUser.toBuilder().identificationNumber(987654321).roleId(1).build();
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");

        when(userRepository.findByEmailAndPassword(userWithId.getEmail(), "123"))
                .thenReturn(Mono.just(userWithId));
        when(roleRepository.findById(userWithId.getRoleId()))
                .thenReturn(Mono.just(role));
        when(jwtProvider.generateToken(any(User.class)))
                .thenReturn(Mono.just("mocked-jwt-token"));

        StepVerifier.create(authenticationUseCase.login(userWithId.getEmail(), "123"))
                .expectNextMatches(auth -> auth.getUsername().equals(userWithId.getEmail()))
                .verifyComplete();
    }
}
