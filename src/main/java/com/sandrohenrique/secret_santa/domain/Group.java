package com.sandrohenrique.secret_santa.domain;

import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;
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

    private String name;

    @ElementCollection
    private Set<Long> friendIds;

    private boolean isDrawn;

    public Group(GroupDTO data) {
        this.name = data.name();
        this.friendIds = data.friendIds();
    }

}
