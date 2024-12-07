package com.sandrohenrique.secret_santa.dtos;

import java.util.Set;

public record GroupFriendIdsDTO(Long groupId, Set<Long> friendIds) {
}
