package com.crediya.model.jwtprovider;

import com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface JwtProvider {
    Mono<String> generateToken(User user);
}
