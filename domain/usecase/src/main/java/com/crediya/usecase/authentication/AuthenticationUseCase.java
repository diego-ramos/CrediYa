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
        Mono<Boolean> emailExists = userRepository.findByEmail(user.getEmail())
                .hasElement(); // true if user exists

        Mono<Boolean> idExists = userRepository.findByIdNumber(user.getIdNumber())
                .hasElement(); // true if id exists

        return Mono.zip(emailExists, idExists)
                .flatMap(tuple -> {
                    boolean emailTaken = tuple.getT1();
                    boolean idTaken = tuple.getT2();

                    if (emailTaken) {
                        return Mono.error(new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED));
                    }
                    if (idTaken) {
                        return Mono.error(new BusinessException(BusinessErrorMessage.ID_ALREADY_REGISTERED));
                    }

                    return userRepository.save(user);
                });
    }
}
