package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to create a new user in the Secret Santa system.")
public record CreateUserDTO(
        @Schema(description = "User's name", example = "sandrohmt123")
        String login,

        @Schema(description = "User's password", example = "StrongPassword1234")
        String password) {
}
