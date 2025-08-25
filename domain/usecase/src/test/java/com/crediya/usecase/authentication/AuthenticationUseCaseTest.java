package com.crediya.usecase.authentication;

import com.crediya.model.user.User;
import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.message.BusinessErrorMessage;
import com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationUseCaseTest {

    private UserRepository userRepository;
    private AuthenticationUseCase authenticationUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        authenticationUseCase = new AuthenticationUseCase(userRepository);
    }

    @Test
    void mustRegisterUserSuccessfully() {
        User user = new User();
        user.setBaseSalary(5_000_000L);

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        Mono<User> result = authenticationUseCase.registerUser(user);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getBaseSalary() == 5_000_000)
                .verifyComplete();

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void mustFailWhenSalaryIsNegative() {
        User user = new User();
        user.setBaseSalary(-1000L);

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
        user.setBaseSalary(20_000_000L);

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
}

