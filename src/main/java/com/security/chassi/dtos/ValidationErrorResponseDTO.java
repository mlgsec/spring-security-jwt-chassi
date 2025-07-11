package com.security.chassi.dtos;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> messages
) {}
