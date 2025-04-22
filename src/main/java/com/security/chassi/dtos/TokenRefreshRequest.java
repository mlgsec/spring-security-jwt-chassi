package com.security.chassi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @NotBlank(message = "O token de refresh é obrigatório.")
    private String refreshToken;
}
