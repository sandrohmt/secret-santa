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
import org.springframework.boot.test.web.server.LocalServerPort;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GroupControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUserCreator")
    private TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUserCreator")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost" + port)
                    .basicAuthentication("sandrohmt", "$2a$10$AXpQy1qlGN7O0snJy5MnZuwu6QIVsn2G4tRJwPtmqU4KYGIDCCVBW");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("findGroupById returns a Group with status 200 when successful")
    void findGroupById_ReturnGroupWithStatus200_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);

        User user = User.builder()
                .login("sandrohmt")
                .password("$2a$10$AXpQy1qlGN7O0snJy5MnZuwu6QIVsn2G4tRJwPtmqU4KYGIDCCVBW")
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupWithFriendsDTO expectedDTO = new GroupWithFriendsDTO(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friends);

        ResponseEntity<GroupWithFriendsDTO> response = testRestTemplate.getForEntity("/groups/{id}", GroupWithFriendsDTO.class, groupId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());

    }
}
