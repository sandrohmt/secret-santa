package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.services.GroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest
public class GroupControllerTest {

    @InjectMocks
    GroupController groupController;

    @Mock
    GroupService groupService;

    @Test
    @DisplayName("findGroupById returns a Group when successful")
    void findGroupById_ReturnGroup_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupWithFriendsDTO expectedDTO = new GroupWithFriendsDTO(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friends);
        when(groupService.findGroupWithFriendsById(groupId)).thenReturn(expectedDTO);

        ResponseEntity<GroupWithFriendsDTO> responseEntity = groupController.findGroupById(groupId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
    }
}
