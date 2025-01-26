package com.sandrohenrique.secret_santa.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sandrohenrique.secret_santa.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        if (user == null || user.getLogin() == null) {
            throw new IllegalArgumentException("Login invalido");
        }

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("secret-santa")
                    .withSubject(user.getLogin())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            System.out.println("Token Generate: " + token);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            if (token.startsWith("Basic ")) {
                String base64Token = token.substring(6);
                byte[] decodedBytes = Base64.getDecoder().decode(base64Token);
                token = new String(decodedBytes);
            }

            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("secret-santa")
                    .build();

            System.out.println("Token Validate: " + token);

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
    } catch (JWTVerificationException exception) {
        throw new RuntimeException("Erro ao validar o token: " + exception.getMessage(), exception);
    }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
