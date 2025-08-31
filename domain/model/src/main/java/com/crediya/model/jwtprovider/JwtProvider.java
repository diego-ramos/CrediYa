package com.crediya.model.jwtprovider;

import com.crediya.model.user.User;

public interface JwtProvider {
    String generateToken(User user);
}
