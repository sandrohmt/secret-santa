package com.sandrohenrique.secret_santa.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private String id;

    private String name;

    @AllArgsConstructor
    @Getter
    public enum Values {
        ADMIN(1l),
        USER(2l);

        long id;
    }
}
