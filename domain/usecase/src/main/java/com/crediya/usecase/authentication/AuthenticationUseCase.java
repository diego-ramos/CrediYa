package com.crediya.usecase.authentication;

import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.message.BusinessErrorMessage;
import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class AuthenticationUseCase {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    private final UserRepository userRepository;

    public Mono<User> registerUser(User user){
        if (user.getBaseSalary().compareTo(MIN_SALARY) < 0 ||
                user.getBaseSalary().compareTo(MAX_SALARY) > 0) {
            return Mono.error(new BusinessException(BusinessErrorMessage.SALARY_OUT_OF_RANGE));
        }
        Mono<Boolean> emailExists = userRepository.findByEmail(user.getEmail())
                .hasElement(); // true if user exists

        Mono<Boolean> identificationNumberExists = userRepository.findByIdentificationNumber(user.getIdentificationNumber())
                .hasElement(); // true if id exists

        return Mono.zip(emailExists, identificationNumberExists)
                .flatMap(tuple -> {
                    boolean emailTaken = tuple.getT1();
                    boolean idTaken = tuple.getT2();

                    if (emailTaken) {
                        return Mono.error(new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED));
                    }
                    if (idTaken) {
                        return Mono.error(new BusinessException(BusinessErrorMessage.IDENTIFICATION_NUMBER_ALREADY_REGISTERED));
                    }

                    return userRepository.save(user);
                });
    }
}
