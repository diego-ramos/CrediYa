package com.crediya.api.config;

import com.crediya.api.security.*;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CustomAuthenticationEntryPoint entryPoint,
                                                         CustomAccessDeniedHandler accessDeniedHandler,
                                                         CustomAuthorizationManager customAuthorizationManager) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v*/usuarios/login").permitAll()
                        .pathMatchers("/webjars/swagger-ui/*").permitAll()
                        .pathMatchers("/v3/api-docs/*").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/api/v*/usuarios/identification-number/*").authenticated()
                        .anyExchange().access(customAuthorizationManager) // dynamic DB permissions
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(entryPoint) // triggers for invalid/missing token
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler) // triggers for insufficient roles
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        var decoder = NimbusReactiveJwtDecoder.withSecretKey(jwtService.getSigningKey()).build();

        // Wrap decoding errors so Spring Security sees them as AuthenticationException
        return jwt -> decoder.decode(jwt)
                .onErrorMap(e -> new AuthenticationException("Invalid or expired token", e) {});
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = jwtService.getSigningKey();
        return new NimbusJwtEncoder(new ImmutableSecret<>(key)); // ✅ encoder for creating tokens
    }
}


