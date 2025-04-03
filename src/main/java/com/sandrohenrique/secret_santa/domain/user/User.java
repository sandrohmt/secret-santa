package com.sandrohenrique.secret_santa.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Table(name = "users")
@Entity(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class User { // Vem de dentro do Spring Security e é usada pra identificar uma classe que represente um usuário que vai ser autenticado na nossa aplicação
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
