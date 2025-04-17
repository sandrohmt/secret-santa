package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO returned after a succesful login, containing the JWT token and its expiration time.")
public record LoginResponseDTO(

        @Schema(description = "Access token to be used in authenticated requests", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String accesstoken,

        @Schema(description = "Time in seconds until the token expires", example = "600")
        Long expiresIn) {
}
