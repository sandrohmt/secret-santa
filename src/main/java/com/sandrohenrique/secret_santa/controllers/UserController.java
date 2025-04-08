package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.user.Role;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.dtos.CreateUserDTO;
import com.sandrohenrique.secret_santa.repositories.RoleRepository;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
@Tag(name = "Users", description = "Endpoints for managing users registration and list users in the Secret Santa system.")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @PostMapping("register")
    @Operation(summary = "Create a new user", description = "Register a new user with role user in the Secret Santa system.")
    @ApiResponse(responseCode = "201", description = "Successfully create a new user.")
    @ApiResponse(responseCode = "422", description = "User is already created.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while creating the new user.")
    public ResponseEntity<Void> createUser(@RequestBody  CreateUserDTO data) {
        Role userRole = roleRepository.findByName(Role.Values.USER.name());

        Optional<User> userFromDB = userRepository.findByLogin(data.login());
        if (userFromDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User already exists with this login.");
        }

        var user = new User();
        user.setLogin(data.login());
        user.setPassword(bCryptPasswordEncoder.encode(data.password()));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("users")
    @Operation(summary = "List all users", description = "Returns a list of all users registered in the Secret Santa system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while listing all users.")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
