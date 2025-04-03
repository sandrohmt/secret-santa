package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.user.Role;

public record RegisterDTO(String login, String password, Role role) {
}
