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
            .login("adm")
            .password("$2a$10$BKXeW45W8RxLK7tNQyJu/.LCehNVP7yzMVQc1AasShugfy5wkNU4W")
            .role(UserRole.ADMIN)
            .build();

    private static final User USER = User.builder()
            .login("sandrohmtuser")
            .password("2a$10$QMRJfqF6rOhOFES0LCzgkOHcoECrG0Isg6n2T5PivUs6pkcI1v73i")
            .role(UserRole.USER)
            .build();

    @Lazy
    @TestConfiguration
    static class Config {
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("adm", "1234");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("sandrohmtuser", "123456789");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("findGroupById returns a Group with status 200 when successful")
    void findGroupById_ReturnGroupWithStatus200_WhenSuccessful() {
        userRepository.save(ADMIN);

        Long groupId = 1L;

        ResponseEntity<GroupWithFriendsDTO> response = testRestTemplateRoleAdmin.getForEntity("/groups/by-id/{id}", GroupWithFriendsDTO.class, groupId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());

    }
}
