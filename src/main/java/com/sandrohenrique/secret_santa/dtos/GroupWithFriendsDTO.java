package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.Friend;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO used to return detailed information about a group in the Secret Santa system, including the list of associated friends.")
public record GroupWithFriendsDTO(

        @Schema(description = "Group Id", example = "1")
        Long id,

        @Schema(description = "Group name", example = "Family christmas")
        String name,

        @Schema(description = "Group event location", example = "123 Main Street, Springfield, IL, 62704, USA")
        String eventLocation,

        @Schema(description = "Group event date", example = "2025-12-25")
        LocalDate eventDate,

        @Schema(description = "Maximum amount allowed for gift spending", example = "200.0")
        Float spendingCap,

        @Schema(description = "List of friend participating in the group", example = "[Sandro, John, Luke]")
        List<Friend> friends) {
}
