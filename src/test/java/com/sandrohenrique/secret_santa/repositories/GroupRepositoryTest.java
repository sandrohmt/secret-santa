package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
class GroupRepositoryTest {
    @Autowired
    GroupRepository groupRepository;

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L,3L), false);

        assertThrows(ConstraintViolationException.class, () -> groupRepository.save(group));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when eventLocation is empty")
    void save_ThrowsConstraintViolationException_WhenEventLocationIsEmpty() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "", eventDate, 100F, Set.of(1L,2L,3L), false);

        assertThrows(ConstraintViolationException.class, () -> groupRepository.save(group));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when eventDate is empty")
    void save_ThrowsConstraintViolationException_WhenEventDateIsEmpty() {
        Long groupId = 1L;
        LocalDate eventDate = null;
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L,3L), false);

        assertThrows(ConstraintViolationException.class, () -> groupRepository.save(group));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when spendingCap is empty")
    void save_ThrowsConstraintViolationException_WhenSpendingCapIsEmpty() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, null, Set.of(1L,2L,3L), false);

        assertThrows(ConstraintViolationException.class, () -> groupRepository.save(group));
    }
}