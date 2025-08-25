package com.crediya.usecase.authentication;

import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationUseCase {
    private final UserRepository userRepository;

    public Mono<User> registerUser(User user){
        return userRepository.save(user);
    }

//    private <T> Mono<T> getBusinessError(BusinessErrorMessage businessErrorMessage) {
//        return Mono.error(getBusinessException(businessErrorMessage));
//    }
//
//    private BusinessException getBusinessException(BusinessErrorMessage businessErrorMessage) {
//        return new BusinessException(businessErrorMessage);
//    }

}
