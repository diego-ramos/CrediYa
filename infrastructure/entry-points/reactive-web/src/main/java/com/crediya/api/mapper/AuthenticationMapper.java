package com.crediya.api.mapper;

import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.api.dto.UserDTO;
import com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorizationRequestMapper {

    User toDomain(RegisterUserRequest dto);
}
