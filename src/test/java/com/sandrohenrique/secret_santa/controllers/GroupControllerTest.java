package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.services.FriendService;
import com.sandrohenrique.secret_santa.services.GroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest
class GroupControllerTest {

    @InjectMocks
    GroupController groupController;

    @Mock
    GroupService groupService;

    @Mock
    FriendService friendService;

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
    @DisplayName("findGroupById returns 404 not found when EntityNotFoundException is thrown")
    void findGroupById_ReturnsNotFound_WhenEntityNotFoundExceptionThrown() {
        doThrow(new EntityNotFoundException("Grupo com ID fornecido não encontrado!"))
                .when(groupService.findGroupById(1L));

        ResponseEntity<Void> response = null;
        try {
            groupController.findGroupById(1L);
        } catch (EntityNotFoundException e) {
        response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
        Set<Long> friendIds = Set.of(1L, 2L);

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

    @Test
    @DisplayName("addFriendsById add friends to group with status 200 when successful")
    void addFriendsById_AddFriendsToGroupWithStatus200_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        Set<Long> friendIds = Set.of(1L, 2L);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, friendIds);

        when(groupService.findGroupById(groupId)).thenReturn(expectedGroup);
        when(friendService.findFriendById(1L)).thenReturn(friend1);
        when(friendService.findFriendById(2L)).thenReturn(friend2);

        ResponseEntity<String> responseEntity = groupController.addFriendsById(data);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());

        verify(groupService, times(1)).addFriendsById(data);
    }

    @Test
    @DisplayName("drawFriends draw Friends with status 200 when successful")
    void drawFriends_DrawFriendsWithStatus200_WhenSuccessful() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(), false);

        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        Friend friend3 = new Friend(3L, "Arthur", "Oliveira", "arthuroliveira@gmail.com", List.of("Kindle", "Livro"), null);

        when(groupService.findGroupById(groupId)).thenReturn(expectedGroup);
        when(friendService.findAllFriendsById(expectedGroup.getFriendIds())).thenReturn(List.of(friend1, friend2, friend3));

        ResponseEntity<GroupWithFriendsDTO> responseEntity = groupController.drawFriends(groupId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);

        verify(groupService, times(1)).drawFriends(groupId);
    }

    @Test
    @DisplayName("deleteFriendsInGroup delete Friends from Group with status 204 when successful")
    void deleteFriendsInGroup_DeleteFriendsFromGroupWithStatus204_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(Set.of(1L, 2L)), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of(friend1.getId(), friend2.getId()));

        when(groupService.findGroupById(groupId)).thenReturn(expectedGroup);

        ResponseEntity<Void> responseEntity = groupController.deleteFriendsInGroup(data);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);

        verify(groupService, times(1)).deleteFriendsInGroup(data);
    }
}
