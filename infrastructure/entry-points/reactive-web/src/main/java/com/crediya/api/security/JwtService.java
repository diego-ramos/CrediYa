package com.crediya.api.security;

import com.crediya.model.jwtprovider.JwtProvider;
import com.crediya.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService implements JwtProvider {

    private final SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRole().getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key)
                .compact();
    }

    public SecretKey getSigningKey() {
        return key;
    }
}

