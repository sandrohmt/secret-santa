package com.sandrohenrique.secret_santa.dtos;

import java.util.Set;

public record GroupFriendDTO(Long groupId, Set<Long> friendIds) {
}
