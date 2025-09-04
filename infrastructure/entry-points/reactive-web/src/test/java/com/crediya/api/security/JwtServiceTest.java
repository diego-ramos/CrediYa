package com.crediya.api.security;

import com.crediya.model.permission.Permission;
import com.crediya.model.permission.gateways.PermissionRepository;
import com.crediya.model.role.Role;
import com.crediya.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private PermissionRepository permissionRepository;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        permissionRepository = mock(PermissionRepository.class);
        jwtService = new JwtService(permissionRepository);
        // inject secret manually
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "12345678901234567890123456789012");
    }

    @Test
    void generateToken_ShouldContainExpectedClaims() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRoleId(1);
        user.setRole(new Role(1, "ADMIN"));

        Permission permission = mock(Permission.class);
        when(permission.getPath()).thenReturn("/api/test");
        when(permission.getMethod()).thenReturn("GET");
        when(permission.getRoleName()).thenReturn("CLIENTE");

        when(permissionRepository.findByRoleId(1))
                .thenReturn(Flux.just(permission));

        // Act
        StepVerifier.create(jwtService.generateToken(user))
                .assertNext(token -> {
                    // Parse JWT
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(jwtService.getSigningKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    assertThat(claims.getSubject()).isEqualTo("test@example.com");
                    assertThat(claims.get("roles", List.class)).containsExactly("ADMIN");

                    List<Map<String, Object>> perms = claims.get("permissions", List.class);
                    assertThat(perms).hasSize(1);
                    assertThat(perms.get(0).get("path")).isEqualTo("/api/test");
                    assertThat(perms.get(0).get("method")).isEqualTo("GET");
                    assertThat(perms.get(0).get("role")).isEqualTo("CLIENTE");
                })
                .verifyComplete();
    }

    @Test
    void getSigningKey_ShouldReturnValidKey() {
        // Act
        SecretKey key = jwtService.getSigningKey();

        // Assert
        assertThat(key).isNotNull();
        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
    }

    @Test
    void buildClaims_ShouldIncludeRolesAndPermissions() {
        // Arrange
        User user = new User();
        user.setRoleId(2);
        user.setRole(new Role(2, "CLIENT"));

        Permission permission1 = mock(Permission.class);
        when(permission1.getPath()).thenReturn("/api/one");
        when(permission1.getMethod()).thenReturn("POST");
        when(permission1.getRoleName()).thenReturn("ROLE_CLIENT");

        Permission permission2 = mock(Permission.class);
        when(permission2.getPath()).thenReturn("/api/two");
        when(permission2.getMethod()).thenReturn("GET");
        when(permission2.getRoleName()).thenReturn("ROLE_CLIENT");

        when(permissionRepository.findByRoleId(2))
                .thenReturn(Flux.just(permission1, permission2));

        // Act
        StepVerifier.create(jwtService.generateToken(user))
                .assertNext(token -> {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(jwtService.getSigningKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    assertThat(claims.get("roles", List.class)).containsExactly("CLIENT");
                    List<Map<String, Object>> perms = claims.get("permissions", List.class);
                    assertThat(perms).hasSize(2);
                })
                .verifyComplete();
    }
}
