package com.sandrohenrique.secret_santa.dtos;

import java.util.List;

public record FriendDTO(String firstName, String lastName, List<String> wishlist) {
}
