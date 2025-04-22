package com.security.chassi.dtos;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private final String accessToken;
    private final String refreshToken;

    public AuthenticationResponse(String jwt, String refreshToken) {
        this.accessToken = jwt;
        this.refreshToken = refreshToken;
    }

}
