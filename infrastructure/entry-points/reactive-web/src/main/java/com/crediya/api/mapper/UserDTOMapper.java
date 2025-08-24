package com.crediya.api.mapper;

import com.crediya.api.dto.CreateUserDTO;
import com.crediya.api.dto.EditUserDTO;
import com.crediya.api.dto.OutUserDTO;
import com.crediya.model.user.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    OutUserDTO toResponse(User user);
    List<OutUserDTO> toResponseList(List<User> users);
    User toModel(EditUserDTO editUserDTO);
    User toModel(CreateUserDTO createUserDTO);
}
