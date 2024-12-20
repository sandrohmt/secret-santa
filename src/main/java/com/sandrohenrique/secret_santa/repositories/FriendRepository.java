package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
     Optional<Friend> findById(Long id);
     Optional<Friend> findByEmail(String email);
     List<Friend> findAllById(Iterable<Long> id);
     List<Friend> findAll();
}
