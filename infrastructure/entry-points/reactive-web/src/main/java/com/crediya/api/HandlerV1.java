package com.crediya.api;

import com.crediya.api.dto.UserDTO;
import com.crediya.api.mapper.UserMapper;
import com.crediya.model.user.User;
import com.crediya.usecase.authentication.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HandlerV1 {
    private  final AuthenticationUseCase authenticationUseCase;
    private final UserMapper userMapper;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
       return  serverRequest.bodyToMono(UserDTO.class)
               .map(userMapper::toModel)
               .flatMap(authenticationUseCase::registerUser)
               .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
    }

//    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
//        // useCase2.logic();
//        return ServerResponse.ok().bodyValue("");
//    }
//
//    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
//        // useCase.logic();
//        return ServerResponse.ok().bodyValue("");
//    }
}
