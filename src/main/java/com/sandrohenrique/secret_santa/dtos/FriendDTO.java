package com.sandrohenrique.secret_santa.dtos;

import java.util.List;

public record FriendDTO(String firstName, String lastName, String email, List<String> wishlist) {
}
