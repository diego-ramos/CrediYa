package com.crediya.api.security;

import com.crediya.api.Constants;
import com.crediya.api.security.CustomAccessDeniedHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CustomAccessDeniedHandler();
    }

    @Test
    void handle_shouldReturnForbiddenJsonResponse() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest.get("/forbidden").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        // when
        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        // then
        MockServerHttpResponse response = exchange.getResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        String body = response.getBodyAsString().block();
        assertThat(body).contains("\"status\":403");
        assertThat(body).contains("\"error\":\"" + Constants.FORBIDDEN + "\"");
        assertThat(body).contains("\"message\":\"" + Constants.YOU_DONT_HAVE_PERMISSION_TO_ACCESS + "\"");
    }
}

