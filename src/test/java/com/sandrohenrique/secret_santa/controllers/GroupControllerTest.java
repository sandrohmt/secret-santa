package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
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
    @DisplayName("findGroupById returns a Group with status 200 when successful")
    void findGroupById_ReturnGroupWithStatus200_WhenSuccessful() {
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

    @Test
    @DisplayName("findGroupByName returns a Group with status 200 when successful")
    void findGroupByName_ReturnGroupWithStatus200_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);

        Long groupId = 1L;
        String groupName = "Amigo Secreto de Fim de Ano";
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupWithFriendsDTO expectedDTO = new GroupWithFriendsDTO(groupId, groupName, "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friends);
        List<GroupWithFriendsDTO> DTOList = List.of(expectedDTO);

        when(groupService.findGroupWithFriendsByName(groupName)).thenReturn(DTOList);

        ResponseEntity<List<GroupWithFriendsDTO>> responseEntity = groupController.findGroupByName(groupName);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(DTOList, responseEntity.getBody());
        Assertions.assertEquals(DTOList.size(), responseEntity.getBody().size());
    }

    @Test
    @DisplayName("createGroup return Group with status 201 when successful")
    void createGroup_ReturnGroupWithStatus201_WhenSuccessful() {
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupDTO groupDTO = new GroupDTO("Natal em família", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friendIds);
        Group expectedGroup = new Group(groupDTO);

        when(groupService.createGroup(groupDTO)).thenReturn(expectedGroup);

        ResponseEntity<Group> responseEntity = groupController.createGroup(groupDTO);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(expectedGroup, responseEntity.getBody());

        verify(groupService, times(1)).createGroup(groupDTO);
    }
}
