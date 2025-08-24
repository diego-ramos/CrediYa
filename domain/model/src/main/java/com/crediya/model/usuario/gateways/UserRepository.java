package com.crediya.model.usuario.gateways;

import com.crediya.model.usuario.User;

public interface UserRepository {
    void registerUser(User user);
}
