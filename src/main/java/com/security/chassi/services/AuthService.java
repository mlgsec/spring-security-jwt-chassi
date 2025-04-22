package com.security.chassi.services;

import com.security.chassi.config.JwtUtil;
import com.security.chassi.dtos.*;
import com.security.chassi.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        User user = userService.findByEmail(authenticationRequest.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        JwtResponse jwtResponse = new JwtResponse(jwt, refreshToken.getToken());
        return ResponseEntity.ok(jwtResponse); // Agora estamos retornando o tipo correto
    }


    public ResponseEntity<JwtResponse> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    // Verificando a expiração do refresh token
                    refreshTokenService.verifyExpiration(refreshToken);

                    // Obtendo o usuário corretamente
                    User user = refreshToken.getUser(); // Certifique-se de que o tipo de user é o esperado

                    // Gerando o novo JWT
                    String token = jwtUtil.generateToken(user.getEmail());

                    // Criando a resposta com o novo token e o refresh token original
                    JwtResponse jwtResponse = new JwtResponse(token, requestRefreshToken);
                    return ResponseEntity.ok(jwtResponse);  // Retornando a resposta com tipo específico
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

}
