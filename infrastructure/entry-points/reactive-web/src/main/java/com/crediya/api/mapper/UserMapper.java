package com.crediya.api.mapper;

import com.crediya.api.dto.UserDTO;
import com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDTO toDTO(User user);
    User toModel(UserDTO userDTO);
}
