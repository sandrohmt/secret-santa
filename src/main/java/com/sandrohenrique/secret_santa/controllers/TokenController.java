package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.user.Role;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.dtos.LoginRequestDTO;
import com.sandrohenrique.secret_santa.dtos.LoginResponseDTO;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoint for handling user authentication and JWT token generation.")

public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("login")
    @Operation(summary = "Authenticate user and generate JWT token", description =  "Authenticates a user using login credentials and returns a JWT token to be used in protected requests.")
    @ApiResponse(responseCode = "200", description = "Login successful. JWT token returned.")
    @ApiResponse(responseCode = "401", description = "Invalid login credentials.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while authenticating")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> user = userRepository.findByLogin(loginRequest.login());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Invalid login or password!");
        }

        var now = Instant.now();
        var expiresIn = 600L;

        var scopes = user.get().getRoles().
                stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder() // Configurar os atributos do json
                .issuer("mybackend")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(jwtValue, expiresIn));
    }
}
