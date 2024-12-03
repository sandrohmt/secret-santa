package com.sandrohenrique.secret_santa.domain;

import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity(name = "groups")
@Table(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "The firstName cannot be empty")
    private String name;

    @NotEmpty(message = "The eventLocation cannot be empty")
    private String eventLocation;

    @NotEmpty(message = "The eventDate cannot be empty")
    private LocalDate eventDate;

    @NotEmpty(message = "The spendingCap cannot be empty")
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
