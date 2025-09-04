package com.crediya.r2dbc;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.exception.message.TechnicalErrorMessage;
import com.crediya.model.permission.Permission;
import com.crediya.model.permission.gateways.PermissionRepository;
import com.crediya.r2dbc.data.PermissionEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class PermissionReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Permission/* change for domain model */,
        PermissionEntity/* change for adapter model */,
        String,
        PermissionReactiveRepository
        >

    implements PermissionRepository
{
    public PermissionReactiveRepositoryAdapter(PermissionReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Permission.class));
    }

    @Override
    public Flux<Permission> findByServerId(String serverId) {
        return repository
                .findAllByServerId(serverId)
                .map(this::toEntity)
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.PERMISSION_BY_SERVER_ID_FIND));
    }

    @Override
    public Flux<Permission> findByRoleId(Integer roleId) {
        return repository
                .findAllByRoleId(roleId)
                .map(this::toEntity)
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.PERMISSION_BY_SERVER_ID_FIND));
    }
}
