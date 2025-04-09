package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to authenticate a user in the Secret Santa system")
public record LoginRequestDTO(

        @Schema(description = "User's login name", example = "sandrohmt123")
        String login,

        @Schema(description = "User's password", example = "StrongPassword1234")
        String password) {
}
