package com.crediya.r2dbc;

import com.crediya.model.permission.Permission;
import com.crediya.r2dbc.data.PermissionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermissionReactiveRepositoryAdapterTest {

    @InjectMocks
    PermissionReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    PermissionReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void findByServerId() {
        Permission permission = new Permission();
        permission.setId(123L);

        PermissionEntity entity = new PermissionEntity();
        entity.setId(123L);

        when(repository.findAllByServerId(anyString())).thenReturn(Flux.just(entity));
        when(mapper.map(any(PermissionEntity.class), eq(Permission.class))).thenReturn(permission);

        Flux<Permission> result = repositoryAdapter.findByServerId("123");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(permission.getId()))
                .verifyComplete();

        verify(repository).findAllByServerId(anyString());
        verify(mapper).map(entity, Permission.class);
    }

    @Test
    void findByRoleId() {
        Permission permission = new Permission();
        permission.setId(123L);

        PermissionEntity entity = new PermissionEntity();
        entity.setId(123L);

        when(repository.findAllByRoleId(anyInt())).thenReturn(Flux.just(entity));
        when(mapper.map(any(PermissionEntity.class), eq(Permission.class))).thenReturn(permission);

        Flux<Permission> result = repositoryAdapter.findByRoleId(123);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(permission.getId()))
                .verifyComplete();

        verify(repository).findAllByRoleId(anyInt());
        verify(mapper).map(entity, Permission.class);
    }
}
