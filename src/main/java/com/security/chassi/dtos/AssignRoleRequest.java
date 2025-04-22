package com.security.chassi.dtos;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(@NotBlank String email, @NotBlank String roleName) {}
