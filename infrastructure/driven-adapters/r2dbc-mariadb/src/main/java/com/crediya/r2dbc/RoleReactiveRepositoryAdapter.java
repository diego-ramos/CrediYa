package com.crediya.r2dbc;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.exception.message.TechnicalErrorMessage;
import com.crediya.model.role.Role;
import com.crediya.model.role.gateways.RoleRepository;
import com.crediya.model.user.User;
import com.crediya.r2dbc.data.RoleEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Role/* change for domain model */,
        RoleEntity/* change for adapter model */,
        String,
        RoleReactiveRepository
        >

        implements RoleRepository
{
    protected RoleReactiveRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Role> findById(Integer id) {
         return repository.findFirstById(id)
                .map(this::toEntity)      // convert entity -> domain
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.USER_ROLE_FIND));
    }
}
