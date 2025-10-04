package com.crediya.api.mapper;

import com.crediya.api.dto.RegisterUserRequest;
import com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    User toModel(RegisterUserRequest dto);
}
