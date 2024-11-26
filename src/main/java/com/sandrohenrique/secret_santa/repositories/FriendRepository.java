package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
     Optional<Friend> findFriendById(Long id);

}
