package com.sandrohenrique.secret_santa.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record GroupDTO(String name, String eventLocation, LocalDate eventDate, Float spendingCap, Set<Long> friendIds) {
}
