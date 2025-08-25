package com.crediya.r2dbc;

import com.crediya.model.exception.TechnicalException;
import com.crediya.model.exception.message.TechnicalErrorMessage;
import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import com.crediya.r2dbc.data.UserEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter  extends ReactiveAdapterOperations<
        User/* change for domain model */,
        UserEntity/* change for adapter model */,
        String,
        UserReactiveRepository
        >

        implements UserRepository
{
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

    @Override
    public Mono<User> findByEmail(String email) {
        return repository
                .findFirstByEmail(email)       // returns Mono<UserEntity>
                .map(this::toEntity)      // convert entity -> domain
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.USER_EMAIL_FIND));
    }

    @Override
    public Mono<User> findByIdNumber(Integer idNumber) {
        return repository
                .findFirstByIdNumber(idNumber)       // returns Mono<UserEntity>
                .map(this::toEntity)      // convert entity -> domain
                .onErrorMap(e -> new TechnicalException(e, TechnicalErrorMessage.USER_ID_FIND));
    }
}
