package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to handle an exception.")
public record ExceptionDTO(
        @Schema(description = "Description of the exception's message", example = "Invalid login or password!")
        String message,

        @Schema(description = "Exception's HTTP status code as a String", example = "200")
        String statusCode) {
}
