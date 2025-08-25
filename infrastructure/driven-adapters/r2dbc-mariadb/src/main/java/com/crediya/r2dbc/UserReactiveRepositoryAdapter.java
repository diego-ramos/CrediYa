package com.crediya.r2dbc;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.exception.message.TechnicalErrorMessage;
import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import com.crediya.r2dbc.data.UserData;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter  extends ReactiveAdapterOperations<
        User/* change for domain model */,
        UserData/* change for adapter model */,
        String,
        UserReactiveRepository
        >

        implements UserRepository
{
   // private final User mapper;

    protected UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        return repository
                .save(toData(user))
                .map(this::toEntity)
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.USER_SAVE));
    }
}
