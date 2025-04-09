package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO used to create a new friend in the Secret Santa system.")
public record FriendDTO(

        @Schema(description = "Friend's name", example = "Sandro")
        String firstName,

        @Schema(description = "Friend's last name", example = "Teixeira")
        String lastName,

        @Schema(description = "Friend's email", example = "sandro@example.com")
        String email,

        @Schema(description = "Friend's wishlist", example = "[\"Tv\", \"Playstation 5\", \"Computer\"]")
        List<String> wishlist) {
}
