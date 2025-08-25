package com.crediya.usecase.authentication;

import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.message.BusinessErrorMessage;
import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationUseCase {
    private final UserRepository userRepository;

    public Mono<User> registerUser(User user){
        if (user.getBaseSalary() < 0 || user.getBaseSalary() > 15000000) {
            return Mono.error(new BusinessException(BusinessErrorMessage.SALARY_OUT_OF_RANGE));
        }
        return userRepository.save(user);
    }
}
