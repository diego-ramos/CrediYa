package com.crediya.api.security;

import com.crediya.api.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.AuthenticationException;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthenticationEntryPoint();
    }

    @Test
    void commence_shouldReturnUnauthorizedJsonResponse() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        AuthenticationException ex = new AuthenticationException("Unauthorized") {};

        // when
        StepVerifier.create(entryPoint.commence(exchange, ex))
                .verifyComplete();

        // then
        MockServerHttpResponse response = exchange.getResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        String body = response.getBodyAsString().block();
        assertThat(body).contains("\"status\":401");
        assertThat(body).contains("\"error\":\"" + Constants.UNAUTHORIZED + "\"");
        assertThat(body).contains("\"message\":\"" + Constants.MUST_PROVIDE_VALID_CREDENTIALS + "\"");
    }
}
