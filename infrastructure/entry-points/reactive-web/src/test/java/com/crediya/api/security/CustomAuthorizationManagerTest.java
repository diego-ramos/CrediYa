package com.crediya.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthorizationManagerTest {

    private CustomAuthorizationManager authorizationManager;

    @BeforeEach
    void setUp() {
        authorizationManager = new CustomAuthorizationManager();
    }

    private Jwt buildJwtWithPermissions(List<Map<String, Object>> permissions) {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "user123", "permissions", permissions)
        );
    }

    private AuthorizationContext buildContext(String path, String method) {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                org.springframework.mock.http.server.reactive.MockServerHttpRequest
                        .method(method, path)
        );
        return new AuthorizationContext(exchange);
    }

    @Test
    void shouldAllowWhenPermissionMatches() {
        Jwt jwt = buildJwtWithPermissions(List.of(
                Map.of("path", "/api/v1/resource/*",
                        "method", "GET",
                        "role", "ROLE_ADMIN")
        ));

        Authentication auth = new TestingAuthenticationToken(jwt,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        AuthorizationContext context = buildContext("/api/v1/resource/123", "GET");

        StepVerifier.create(authorizationManager.check(Mono.just(auth), context))
                .assertNext(decision -> assertThat(decision.isGranted()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenPermissionDoesNotMatch() {
        Jwt jwt = buildJwtWithPermissions(List.of(
                Map.of("path", "/api/v1/other/*",
                        "method", "POST",
                        "role", "ROLE_ADMIN")
        ));

        Authentication auth = new TestingAuthenticationToken(jwt,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        AuthorizationContext context = buildContext("/api/v1/resource/123", "GET");

        StepVerifier.create(authorizationManager.check(Mono.just(auth), context))
                .assertNext(decision -> assertThat(decision.isGranted()).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenNoPermissionsClaim() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "user123") // no permissions claim
        );

        Authentication auth = new TestingAuthenticationToken(jwt,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        AuthorizationContext context = buildContext("/api/v1/resource/123", "GET");

        StepVerifier.create(authorizationManager.check(Mono.just(auth), context))
                .assertNext(decision -> assertThat(decision.isGranted()).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenNotAuthenticated() {
        Authentication unauthenticated = Mockito.mock(Authentication.class);
        Mockito.when(unauthenticated.isAuthenticated()).thenReturn(false);

        AuthorizationContext context = buildContext("/api/v1/resource/123", "GET");

        StepVerifier.create(authorizationManager.check(Mono.just(unauthenticated), context))
                .assertNext(decision -> assertThat(decision.isGranted()).isFalse())
                .verifyComplete();
    }
}
