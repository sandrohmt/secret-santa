package com.sandrohenrique.secret_santa.dtos;

import java.util.List;

public record GroupDTO(String name, List<Long> friendIds) {
}
