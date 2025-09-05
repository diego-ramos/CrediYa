package com.crediya.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJwtAuthenticationConverterTest {

    private CustomJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CustomJwtAuthenticationConverter();
    }

    private Jwt createJwtWithClaims(Map<String, Object> claims) {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims.isEmpty() ? Collections.emptyMap() : claims
        );
    }

    @Test
    void convert_withRoles_shouldReturnAuthorities() {
        // given
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ADMIN", "CLIENTE"));

        Jwt jwt = createJwtWithClaims(claims);

        // when
        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        // then
        StepVerifier.create(result)
                .assertNext(auth -> {
                    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                    assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                            .containsExactlyInAnyOrder("ADMIN", "CLIENTE");
                })
                .verifyComplete();
    }
}
