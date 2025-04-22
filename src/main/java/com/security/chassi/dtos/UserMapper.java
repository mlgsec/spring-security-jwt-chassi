package com.security.chassi.dtos;

import com.security.chassi.user.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail());
    }
}