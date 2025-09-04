package com.crediya.r2dbc;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.user.User;
import com.crediya.r2dbc.data.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustSaveValue() {
        User user = new User();
        user.setIdentificationNumber(123);

        UserEntity entity = new UserEntity();
        entity.setIdentificationNumber(123);

        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(entity);
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdentificationNumber().equals(user.getIdentificationNumber()))
                .verifyComplete();

        verify(mapper).map(user, UserEntity.class);
        verify(repository).save(entity);
        verify(mapper).map(entity, User.class);
    }

    @Test
    void mustPropagateErrorWhenRepositoryFails() {
        User user = new User();
        user.setIdentificationNumber(123);

        UserEntity entity = new UserEntity();
        entity.setIdentificationNumber(123);

        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(entity);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("DB down")));

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof TechnicalException &&
                        ex.getMessage().contains("DB down"))
                .verify();

        verify(mapper).map(user, UserEntity.class);
        verify(repository).save(entity);
    }

    @Test
    void findByEmail() {
        User user = new User();
        user.setIdentificationNumber(123);

        UserEntity entity = new UserEntity();
        entity.setIdentificationNumber(123);

        when(repository.findFirstByEmail(any(String.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        Mono<User> result = repositoryAdapter.findByEmail("aa@aaa.com");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdentificationNumber().equals(user.getIdentificationNumber()))
                .verifyComplete();

        verify(repository).findFirstByEmail(any(String.class));
        verify(mapper).map(entity, User.class);
    }

    @Test
    void findByIdNumber() {
        User user = new User();
        user.setIdentificationNumber(123);

        UserEntity entity = new UserEntity();
        entity.setIdentificationNumber(123);

        when(repository.findFirstByIdentificationNumber(any(Integer.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        Mono<User> result = repositoryAdapter.findByIdentificationNumber(123);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdentificationNumber().equals(user.getIdentificationNumber()))
                .verifyComplete();

        verify(repository).findFirstByIdentificationNumber(any(Integer.class));
        verify(mapper).map(entity, User.class);
    }

    @Test
    void findByEmailAndPassword() {
        User user = new User();
        user.setIdentificationNumber(123);

        UserEntity entity = new UserEntity();
        entity.setIdentificationNumber(123);

        when(repository.findFirstByEmailAndPassword(anyString(), anyString())).thenReturn(Mono.just(entity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        Mono<User> result = repositoryAdapter.findByEmailAndPassword("aa@aaa.com", "123");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdentificationNumber().equals(user.getIdentificationNumber()))
                .verifyComplete();

        verify(repository).findFirstByEmailAndPassword(anyString(), anyString());
        verify(mapper).map(entity, User.class);
    }
}
