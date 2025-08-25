package com.crediya.usecase.authentication;

import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationUseCase {
    private final UserRepository userRepository;

    public Mono<User> registerUser(User user){
        System.out.println("User: "+user.toString());
        return userRepository.save(user);
    }
}
