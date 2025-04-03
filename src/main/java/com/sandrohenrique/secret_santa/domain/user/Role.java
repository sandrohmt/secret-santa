package com.sandrohenrique.secret_santa.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private String id;

    private String name;

    @AllArgsConstructor
    @Getter
    public enum Values {
        ADMIN(1L),
        USER(2L);

        long id;
    }
}
