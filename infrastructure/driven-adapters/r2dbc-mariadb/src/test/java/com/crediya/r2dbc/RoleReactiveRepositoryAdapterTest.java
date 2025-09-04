package com.crediya.r2dbc;

import com.crediya.model.role.Role;
import com.crediya.r2dbc.data.RoleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleReactiveRepositoryAdapterTest {

    @InjectMocks
    RoleReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    RoleReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void findById() {
        Role role = new Role();
        role.setId(123);

        RoleEntity entity = new RoleEntity();
        entity.setId(123);

        when(repository.findFirstById(anyInt())).thenReturn(Mono.just(entity));
        when(mapper.map(any(RoleEntity.class), eq(Role.class))).thenReturn(role);

        Mono<Role> result = repositoryAdapter.findById(123);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(role.getId()))
                .verifyComplete();

        verify(repository).findFirstById(anyInt());
        verify(mapper).map(entity, Role.class);
    }

}
