package com.crediya.usecase.authentication;

import com.crediya.model.exception.BusinessException;
import com.crediya.model.exception.message.BusinessErrorMessage;
import com.crediya.model.jwtprovider.JwtProvider;
import com.crediya.model.role.gateways.RoleRepository;
import com.crediya.model.user.AuthResponse;
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
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;

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

    public Mono<User> getUserByIdentificationNumber(Integer identificationNumber){
        return userRepository.findByIdentificationNumber(identificationNumber);
    }

    public Mono<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Mono<AuthResponse> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .switchIfEmpty(Mono.error(new BusinessException(
                        BusinessErrorMessage.ERROR_LOGIN_USER
                )))
                .flatMap(user ->
                        roleRepository.findById(user.getRoleId())
                                .flatMap(role -> {
                                    user.setRole(role); // enrich user with role
                                    return jwtProvider.generateToken(user)
                                            .map(token -> new AuthResponse(token, user.getEmail(), role.getName()));
                                })
                );
    }
}
