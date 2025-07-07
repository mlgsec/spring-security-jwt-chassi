package com.security.chassi.dtos;

import com.security.chassi.user.User;

public class UserMapper {

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }
}