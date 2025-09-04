package com.crediya.api.security;

import com.crediya.model.jwtprovider.JwtProvider;
import com.crediya.model.permission.gateways.PermissionRepository;
import com.crediya.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService implements JwtProvider {

    private final PermissionRepository permissionRepository;

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<String> generateToken(User user) {
        return buildClaims(user)
                .map(claims -> Jwts.builder()
                        .setSubject(user.getEmail())
                        .addClaims(claims)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                        .compact());
    }

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private Mono<Map<String, Object>> buildClaims(User user) {
        return permissionRepository.findByRoleId(user.getRoleId())
                .collectList()
                .map(permissions -> {
                    Map<String, Object> claims = new HashMap<>();

                    // roles
                    claims.put("roles", List.of(user.getRole().getName()));

                    // permissions (path + method + role)
                    List<Map<String, Object>> perms = permissions.stream()
                            .map(p -> Map.<String, Object>of(
                                    "path", p.getPath(),
                                    "method", p.getMethod(),
                                    "role", p.getRoleName()
                            ))
                            .toList();

                    claims.put("permissions", perms);
                    return claims;
                });
    }
}

