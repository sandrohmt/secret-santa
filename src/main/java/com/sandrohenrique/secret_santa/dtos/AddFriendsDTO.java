package com.sandrohenrique.secret_santa.dtos;

import java.util.List;

public record AddFriendsDTO(Long groupId, List<Long> friendIds) {
}
