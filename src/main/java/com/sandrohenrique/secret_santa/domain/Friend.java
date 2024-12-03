package com.sandrohenrique.secret_santa.domain;

import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

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

    @NotEmpty(message = "The firstName cannot be empty")
    private String firstName;

    @NotEmpty(message = "The lastName cannot be empty")
    private String lastName;

    @NotEmpty(message = "The email cannot be empty")
    @Email
    private String email;

    @NotEmpty(message = "The wishlist cannot be empty")
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
