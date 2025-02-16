package com.sandrohenrique.secret_santa.domain;

import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity(name = "friend_group")
@Table(name = "friend_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "The name cannot be empty")
    private String name;

    @NotEmpty(message = "The eventLocation cannot be empty")
    private String eventLocation;

    @NotNull(message = "The eventDate cannot be null")
    private LocalDate eventDate;

    @NotNull(message = "The spendingCap cannot be null")
    private Float spendingCap;

    @ElementCollection
    private Set<Long> friendIds;

    private boolean isDrawn;

    public Group(GroupDTO data) {
        this.name = data.name();
        this.friendIds = data.friendIds();
        this.eventLocation = data.eventLocation();
        this.eventDate = data.eventDate();
        this.spendingCap = data.spendingCap();
    }

}
