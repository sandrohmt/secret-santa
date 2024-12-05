package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findGroupByName(String name);
    Optional<Group> findGroupById(Long id);
}
