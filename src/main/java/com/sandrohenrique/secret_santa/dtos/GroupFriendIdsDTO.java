package com.sandrohenrique.secret_santa.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "DTO used to manage the association of friends to a group in the Secret Santa system")
public record GroupFriendIdsDTO(

        @Schema(description = "Group Id", example = "1")
        Long groupId,

        @Schema(description = "List of friend Ids to associate with the group", example = "[1, 2, 3]")
        Set<Long> friendIds) {
}
