package com.sandrohenrique.secret_santa.config;

import com.sandrohenrique.secret_santa.domain.user.Role;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.repositories.RoleRepository;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByLogin("ADMIN");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin já existe!");
                },
                () -> {
                    var user = new User();
                    user.setLogin("ADMIN");
                    user.setPassword(bCryptPasswordEncoder.encode("1234"));
                    user.setRoles(Set.of(roleAdmin));
                }
        );
    }
}

