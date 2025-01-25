package com.sandrohenrique.secret_santa.repositories;

import com.sandrohenrique.secret_santa.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, String> {
    User findByLogin(String login);
}
