package com.sandrohenrique.secret_santa.domain;

import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    @ElementCollection
    private List<String> wishlist;

    private Long drawnFriendId;

    public Friend(FriendDTO data) {
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.email = data.email();
        this.wishlist = data.wishlist();
    }
}
