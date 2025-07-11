package com.security.chassi.services;

import com.security.chassi.config.JwtUtil;
import com.security.chassi.dtos.*;
import com.security.chassi.entities.RefreshToken;
import com.security.chassi.entities.User;
import com.security.chassi.exceptions.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil,
                       UserService userService,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseEntity<JwtResponse> getToken(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Usuário ou senha inválidos");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        User user = userService.findByEmail(authenticationRequest.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        JwtResponse jwtResponse = new JwtResponse(jwt, refreshToken.getToken());
        return ResponseEntity.ok(jwtResponse);
    }


    public ResponseEntity<JwtResponse> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    refreshTokenService.verifyExpiration(refreshToken);
                    User user = refreshToken.getUser();
                    String token = jwtUtil.generateToken(user.getEmail());
                    JwtResponse jwtResponse = new JwtResponse(token, requestRefreshToken);
                    return ResponseEntity.ok(jwtResponse);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
