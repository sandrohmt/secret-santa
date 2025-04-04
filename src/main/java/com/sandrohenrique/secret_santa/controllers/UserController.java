package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.user.Role;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.dtos.CreateUserDTO;
import com.sandrohenrique.secret_santa.repositories.RoleRepository;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @PostMapping("register")
    public ResponseEntity<Void> createUser(@RequestBody  CreateUserDTO data) {
        Role userRole = roleRepository.findByName(Role.Values.USER.name());

        Optional<User> userFromDB = userRepository.findByLogin(data.login());
        if (userFromDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new User();
        user.setLogin(data.login());
        user.setPassword(bCryptPasswordEncoder.encode(data.password()));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
