package com.sandrohenrique.secret_santa.dtos;

import com.sandrohenrique.secret_santa.domain.Friend;

import java.time.LocalDate;
import java.util.List;

public record GroupWithFriendsDTO(Long id, String name, String eventLocation, LocalDate eventDate, Float spendingCap,  List<Friend> friends) {
}
