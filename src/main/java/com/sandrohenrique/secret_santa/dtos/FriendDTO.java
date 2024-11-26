package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.Friend;

import java.util.List;

public record FriendDTO(String firstName, String lastName, List<String> wishlist) {
}
