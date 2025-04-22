package com.security.chassi.dtos;

public record JwtResponse(String accessToken, String refreshToken, String tokenType) {
    public JwtResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
