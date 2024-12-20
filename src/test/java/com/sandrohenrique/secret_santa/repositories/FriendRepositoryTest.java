package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Friend;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class FriendRepositoryTest {
    @Autowired
    FriendRepository friendRepository;

    @Test
    @DisplayName("Save throw ConstraintViolationException when firstName is empty")
    void save_ThrowsConstraintViolationException_WhenFirstNameIsEmpty() {
        Friend friend = new Friend(1L, "", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);

        assertThrows(ConstraintViolationException.class, () -> friendRepository.save(friend));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when lastName is empty")
    void save_ThrowsConstraintViolationException_WhenLastNameIsEmpty() {
        Friend friend = new Friend(1L, "Maria", "", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);

        assertThrows(ConstraintViolationException.class, () -> friendRepository.save(friend));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when email is empty")
    void save_ThrowsConstraintViolationException_WhenEmailIsEmpty() {
        Friend friend = new Friend(1L, "Maria", "Silva", "", List.of("Playstation 5", "Celular"), null);

        assertThrows(ConstraintViolationException.class, () -> friendRepository.save(friend));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when wishlist is empty")
    void save_ThrowsConstraintViolationException_WhenWishlistIsEmpty() {
        Friend friend = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of(), null);

        assertThrows(ConstraintViolationException.class, () -> friendRepository.save(friend));
    }
}