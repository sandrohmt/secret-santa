package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.user.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
