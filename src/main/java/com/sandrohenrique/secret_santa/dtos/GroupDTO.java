package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO used to create a new group in the Secret Santa system.")
public record GroupDTO(

        @Schema(description = "Group name", example = "Family christmas")
        String name,

        @Schema(description = "Group event location", example = "123 Main Street, Springfield, IL, 62704, USA")
        String eventLocation,

        @Schema(description = "Group event date", example = "2025-12-25")
        LocalDate eventDate,

        @Schema(description = "Maximum amount allowed for gift spending", example = "200.0")
        Float spendingCap,

        @Schema(description = "List of friend IDs participating in the group", example = "[1L, 2L, 3L]")
        Set<Long> friendIds) {
}
