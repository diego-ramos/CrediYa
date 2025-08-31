package com.crediya.api.config;

import com.crediya.api.security.CustomJwtAuthenticationConverter;
import com.crediya.api.security.JwtService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final String ADMIN_ROLE = "ADMINISTRADOR";
    private static final String CUSTOMER_ROLE = "CLIENTE";
    private static final String REPRESENTATIVE_ROLE = "ASESOR";

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v*/usuarios/login").permitAll()
                        .pathMatchers("/api/v*/usuarios/identification-number/*").authenticated()
                        .pathMatchers("/api/v*/usuarios/register").hasAnyRole(ADMIN_ROLE, REPRESENTATIVE_ROLE) // ✅ role-based restriction
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withSecretKey(jwtService.getSigningKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = jwtService.getSigningKey();
        return new NimbusJwtEncoder(new ImmutableSecret<>(key)); // ✅ encoder for creating tokens
    }
}


