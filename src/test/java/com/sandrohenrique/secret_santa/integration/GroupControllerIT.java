package com.sandrohenrique.secret_santa.integration;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.domain.user.UserRole;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroupControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    private UserRepository userRepository;

    private static final User ADMIN = User.builder()
            .login("sandrohenrique")
            .password("{bcrypt}$2a$10$DbkXIBeObK76JtvYfR0Ss.2m3K67Ku6WXF3LRPc9pfm6bQpb2UAIm")
            .role(UserRole.ADMIN)
            .build();

    private static final User USER = User.builder()
            .login("sandrohmtUser")
            .password("{bcrypt}2a$10$QMRJfqF6rOhOFES0LCzgkOHcoECrG0Isg6n2T5PivUs6pkcI1v73i")
            .role(UserRole.USER)
            .build();

    @Lazy
    @TestConfiguration
    static class Config {
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("sandrohmt", "1234");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("sandrohmtUser", "123456789");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("findGroupById returns a Group with status 200 when successful")
    void findGroupById_ReturnGroupWithStatus200_WhenSuccessful() {
        userRepository.save(ADMIN);

        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);



        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupWithFriendsDTO expectedDTO = new GroupWithFriendsDTO(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friends);

        ResponseEntity<GroupWithFriendsDTO> response = testRestTemplateRoleAdmin.getForEntity("/groups/by-id/{id}", GroupWithFriendsDTO.class, groupId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());

    }
}
