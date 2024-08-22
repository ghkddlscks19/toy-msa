package org.example.userservice.service;

import org.example.userservice.dto.UserDto;
import org.example.userservice.entity.UserEntity;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(String userId);

    Iterable<UserEntity> getUserbyAll();
}
