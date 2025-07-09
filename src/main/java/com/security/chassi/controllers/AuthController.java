package com.security.chassi.controllers;

import com.security.chassi.config.JwtUtil;
import com.security.chassi.dtos.*;
import com.security.chassi.services.AuthService;
import com.security.chassi.services.RefreshTokenService;
import com.security.chassi.services.UserService;
import com.security.chassi.user.Role;
import com.security.chassi.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserService userService,
                          RefreshTokenService refreshTokenService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        User savedUser = userService.saveUser(request);
        return ResponseEntity.ok(UserMapper.toDto(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return authService.getToken(authenticationRequest);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleToUser(@RequestBody AssignRoleRequest request) {
        User user = userService.assignRoleToUser(request.email(), request.roleName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me/roles")
    public ResponseEntity<Set<String>> getMyRoles(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(roles);
    }


}
