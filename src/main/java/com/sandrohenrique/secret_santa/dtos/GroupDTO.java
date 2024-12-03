package com.sandrohenrique.secret_santa.dtos;

import java.util.List;
import java.util.Set;

public record GroupDTO(String name, Set<Long> friendIds) {
}
