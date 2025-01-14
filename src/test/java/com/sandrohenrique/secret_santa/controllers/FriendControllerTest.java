package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.exceptions.EmailAlreadyRegisteredException;
import com.sandrohenrique.secret_santa.services.FriendService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

@SpringBootTest
class FriendControllerTest {

    @InjectMocks
    FriendController friendController;

    @Mock
    FriendService friendService;

    @Test
    @DisplayName("getAllFriends returns a list of Friends with status 200 when successful")
    void getAllFriends_ReturnListOfFriendsWithStatus200_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> expectedFriends = List.of(friend1, friend2);

        when(friendService.getAllFriends()).thenReturn(expectedFriends);

        ResponseEntity<List<Friend>> responseEntity = friendController.getAllFriends();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(expectedFriends.size(), responseEntity.getBody().size());
        Assertions.assertEquals(expectedFriends, responseEntity.getBody());

        verify(friendService, times(1)).getAllFriends();
    }

    @Test
    @DisplayName("getAllFriends returns an empty list when no Friends are found")
    void getAllFriends_ReturnEmptyList_WhenNoFriendsAreFound() {
        when(friendService.getAllFriends()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Friend>> responseEntity = friendController.getAllFriends();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertTrue(Objects.requireNonNull(responseEntity.getBody()).isEmpty());
    }

    @Test
    @DisplayName("createFriend return Friend with status 201 when successful")
    void createFriend_ReturnFriendWithStatus201_WhenSuccessful() {
        FriendDTO friendDTO = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));
        Friend friend = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);

        when(friendService.createFriend(friendDTO)).thenReturn(friend);

        ResponseEntity<Friend> responseEntity = friendController.createFriend(friendDTO);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(friend, responseEntity.getBody());

        verify(friendService, times(1)).createFriend(friendDTO);
    }

    @Test
    @DisplayName("createFriend returns 409 Conflict when EmailAlreadyRegisteredException is thrown")
    void createFriend_ReturnsConflict_WhenEmailAlreadyRegisteredExceptionThrown() {
        FriendDTO friendDTO = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));

        doThrow(new EmailAlreadyRegisteredException("Amigo com email fornecido já cadastrado!"))
                .when(friendService).createFriend(friendDTO);

        ResponseEntity<Void> response = null;
        try {
            friendController.createFriend(friendDTO);
        } catch (EmailAlreadyRegisteredException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        verify(friendService, times(1)).createFriend(friendDTO);
    }

    @Test
    @DisplayName("createFriends return a list of Friends with status 201 when successfull")
    void createFriends_ReturnListOfFriendsWithStatus201_WhenSuccessful() {
        FriendDTO friendDTO1 = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));
        FriendDTO friendDTO2 = new FriendDTO("José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"));
        List<FriendDTO> friendDTOs = List.of(friendDTO1, friendDTO2);

        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> expectedFriends = List.of(friend1, friend2);

        when(friendService.createFriend(friendDTO1)).thenReturn(friend1);
        when(friendService.createFriend(friendDTO2)).thenReturn(friend2);

        ResponseEntity<List<FriendDTO>> responseEntity = friendController.createFriends(friendDTOs);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(expectedFriends.size(), responseEntity.getBody().size());
        Assertions.assertEquals(expectedFriends.get(0).getFirstName(), responseEntity.getBody().get(0).firstName());
        Assertions.assertEquals(expectedFriends.get(0).getLastName(), responseEntity.getBody().get(0).lastName());
        Assertions.assertEquals(expectedFriends.get(0).getEmail(), responseEntity.getBody().get(0).email());
        Assertions.assertEquals(expectedFriends.get(0).getWishlist(), responseEntity.getBody().get(0).wishlist());
        Assertions.assertEquals(expectedFriends.get(1).getFirstName(), responseEntity.getBody().get(1).firstName());
        Assertions.assertEquals(expectedFriends.get(1).getLastName(), responseEntity.getBody().get(1).lastName());
        Assertions.assertEquals(expectedFriends.get(1).getEmail(), responseEntity.getBody().get(1).email());
        Assertions.assertEquals(expectedFriends.get(1).getWishlist(), responseEntity.getBody().get(1).wishlist());
    }

    @Test
    @DisplayName("createFriends returns 409 Conflict when EmailAlreadyRegisteredException is thrown")
    void createFriends_ReturnsConflict_WhenEmailAlreadyRegisteredExceptionThrown() {
        FriendDTO friendDTO1 = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));
        FriendDTO friendDTO2 = new FriendDTO("José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"));
        List<FriendDTO> friendDTOs = List.of(friendDTO1, friendDTO2);


        doThrow(new EmailAlreadyRegisteredException("Amigo com email fornecido já cadastrado!"))
                .when(friendService).createFriend(friendDTO1);

        ResponseEntity<Void> response = null;
        try {
            friendController.createFriends(friendDTOs);
        } catch (EmailAlreadyRegisteredException ex) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).build(); // Simulando a resposta do handler global
        }

        // Assert: verificar o status da resposta
        Assertions.assertNotNull(response); // Certificar-se de que a resposta não é nula
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode()); // Verificar se o status é 409

        verify(friendService, times(1)).createFriend(friendDTO1);
    }
}