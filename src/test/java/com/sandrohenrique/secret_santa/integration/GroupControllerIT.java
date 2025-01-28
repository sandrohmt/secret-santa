package com.sandrohenrique.secret_santa.integration;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.domain.user.User;
import com.sandrohenrique.secret_santa.domain.user.UserRole;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.infra.security.TokenService;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.UserRepository;
import com.sandrohenrique.secret_santa.services.GroupService;
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
import java.util.Set;

import static org.mockito.Mockito.*;


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

    @Autowired
    private GroupService groupService;

    @Autowired
    private FriendRepository friendRepository;

    private static final User ADMIN = User.builder()
            .login("adm")
            .password("$2a$10$BKXeW45W8RxLK7tNQyJu/.LCehNVP7yzMVQc1AasShugfy5wkNU4W")
            .role(UserRole.ADMIN)
            .build();

    private static final User USER = User.builder()
            .login("sandrohmtuser")
            .password("$2a$10$QMRJfqF6rOhOFES0LCzgkOHcoECrG0Isg6n2T5PivUs6pkcI1v73i")
            .role(UserRole.USER)
            .build();

    @Lazy
    @TestConfiguration
    static class Config {
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port, TokenService tokenService, UserRepository userRepository) {
            User admin = userRepository.save(new User("adm", "1234", UserRole.ADMIN));

            String jwtToken = tokenService.generateToken(admin);

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .defaultHeader("Authorization", "Bearer " + jwtToken);
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port, TokenService tokenService, UserRepository userRepository) {
            User user = userRepository.save(new User("sandrohmtuser", "123456789", UserRole.USER));

            String jwtToken = tokenService.generateToken(user);

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .defaultHeader("Authorization", "Bearer " + jwtToken);
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
        new GroupWithFriendsDTO(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friends);

        ResponseEntity<GroupWithFriendsDTO> response = testRestTemplateRoleAdmin.getForEntity("/groups/by-id/{id}", GroupWithFriendsDTO.class, groupId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("createGroup return Group with status 201 when successful")
    void createGroup_ReturnGroupWithStatus201_WhenSuccessful() {
        userRepository.save(ADMIN);
        
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), 2L);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), 1L);
        friendRepository.save(friend1);
        friendRepository.save(friend2);
        Set<Long> friendIds = Set.of(1L, 2L);

        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupDTO groupDTO = new GroupDTO("Natal em família", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friendIds);
        Group expectedGroup = new Group(groupDTO);

        when(groupService.createGroup(groupDTO)).thenReturn(expectedGroup);

        ResponseEntity<GroupWithFriendsDTO> response = testRestTemplateRoleAdmin.postForEntity("/groups/createGroup", groupDTO, GroupWithFriendsDTO.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedGroup, response.getBody());

        verify(groupService, times(1)).createGroup(groupDTO);
    }

}
