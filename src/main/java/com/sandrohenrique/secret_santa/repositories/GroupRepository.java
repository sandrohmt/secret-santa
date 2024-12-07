package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByName(String name);
    Optional<Group> findById(Long id);
}
