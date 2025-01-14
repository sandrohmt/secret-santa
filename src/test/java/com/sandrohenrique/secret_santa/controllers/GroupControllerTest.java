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
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L,3L), false);

        when(groupService.findGroupById(groupId)).thenReturn(expectedGroup);

        ResponseEntity<GroupWithFriendsDTO> responseEntity = groupController.findGroupById(groupId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme