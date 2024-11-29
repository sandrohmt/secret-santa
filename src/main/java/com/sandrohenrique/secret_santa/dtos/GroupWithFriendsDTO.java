package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.Friend;

import java.util.List;

public record GroupWithFriendsDTO(Long id, String name, List<Friend> friends) {
}
