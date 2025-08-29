package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(RegisterUserRequest registerUserRequest);

    void updateUser(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
