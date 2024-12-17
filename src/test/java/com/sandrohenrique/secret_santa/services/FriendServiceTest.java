package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.exceptions.FriendAlreadyInGroupException;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
class FriendServiceTest {

    @InjectMocks
    FriendService friendService;

    @Mock
    private FriendRepository friendRepository;

    @Test
    @DisplayName("getAllFriends returns a list of Friends when successful")
    void getAllFriends_ReturnListOfFriends_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        when(friendRepository.findAll()).thenReturn(List.of(friend1, friend2));

        List<Friend> friends = friendService.getAllFriends();

        Assertions.assertNotNull(friends);
    }

    @Test
    @DisplayName("getAllFriends returns an empty list when no Friends are found")
    void getAllFriends_ReturnEmptyList_WhenNoFriendsAreFound() {
        when(friendRepository.findAll()).thenReturn(Collections.emptyList());

        List<Friend> friends = friendService.getAllFriends();

        Assertions.assertNotNull(friends);
        Assertions.assertTrue(friends.isEmpty());
    }

    @Test
    @DisplayName("saveAllFriends saves all friends when successful")
    void saveAllFriends_SavesAllFriends_WhenSucessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);

        friendService.saveAllFriends(friends);

        verify(friendRepository, times(1)).saveAll(friends);
    }

    @Test
    @DisplayName("createFriend return Friend when successful")
    void createFriend_ReturnFriend_WhenSuccessful() {
        FriendDTO friendDTO = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));

        Friend friend = friendService.createFriend(friendDTO);

        verify(friendRepository, times(1)).save(any(Friend.class));

        Assertions.assertNotNull(friend);
        Assertions.assertEquals( "Maria", friend.getFirstName());
        Assertions.assertEquals("Silva", friend.getLastName());
        Assertions.assertEquals("mariasilva@gmail.com", friend.getEmail());
        Assertions.assertEquals(List.of("Playstation 5", "Celular"), friend.getWishlist());
    }

    @Test
    @DisplayName("createFriend throws FriendAlreadyInGroupException when Friend is already in Group")
    void createFriend_ThrowFriendAlreadyInGroupException_WhenFriendIsAlreadyInGroup() {
        FriendDTO friendDTO = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));
        Friend existingFriend = new Friend(friendDTO);

        when(friendRepository.findByEmail(friendDTO.email())).thenReturn(Optional.of(existingFriend)); // Simulando situação que um amigo já está no grupo

        FriendAlreadyInGroupException thrown = Assertions.assertThrows(FriendAlreadyInGroupException.class, () -> friendService.createFriend(friendDTO));

        Assertions.assertEquals("Amigo com email fornecido já cadastrado!", thrown.getMessage());
        verify(friendRepository, never()).save(any(Friend.class)); // garante que o save não foi chamado pois o email o amigo tinha email repetido
    }

    @Test
    @DisplayName("findFriendById returns a Friend when successful")
    void findFriendById_ReturnFriend_WhenSuccessful() {
        Long friendId = 1L;
        Friend expectedFriend = new Friend(friendId, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(expectedFriend));

        Friend foundFriend =  friendService.findFriendById(1L);

        Assertions.assertEquals(expectedFriend, foundFriend);
        Assertions.assertNotNull(foundFriend);
        verify(friendRepository, times(1)).findById(friendId);
    }

    @Test
    @DisplayName("findFriendById throws EntityNotFoundException when a friend is not found")
    void findFriendById_ThrowEntityNotFoundException_WhenFriendIsNotFound() {
        when(friendRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> friendService.findFriendById(1L));

        Assertions.assertEquals("Amigo com ID fornecido não encontrado!", thrown.getMessage());
    }

    @Test
    @DisplayName("findFriendByEmail returns a Friend when successful")
    void findFriendByEmail_ReturnFriend_WhenSuccessful() {
        String friendEmail = "mariasilva@gmail.com";
        Friend expectedFriend = new Friend(1L, "Maria", "Silva", friendEmail, List.of("Playstation 5", "Celular"), null);

        when(friendRepository.findByEmail(friendEmail)).thenReturn(Optional.of(expectedFriend));

        Friend foundFriend =  friendService.findFriendByEmail(friendEmail);

        Assertions.assertEquals(expectedFriend, foundFriend);
        Assertions.assertNotNull(foundFriend);
        verify(friendRepository, times(1)).findByEmail(friendEmail);
    }

    @Test
    @DisplayName("findFriendByEmail throws EntityNotFoundException when a friend is not found")
    void findFriendByEmail_ThrowEntityNotFoundException_WhenFriendIsNotFound() {
        String friendEmail = "mariasilva@gmail.com";

        when(friendRepository.findByEmail(friendEmail)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> friendService.findFriendByEmail(friendEmail));

        Assertions.assertEquals("Amigo com email fornecido não encontrado!", thrown.getMessage());
    }

    @Test
    @DisplayName("findAllFriendsById returns a list of Friends when successful")
    void findAllFriendsById_ReturnListOfFriend_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Set<Long> friendIds = Set.of(friend1.getId(), friend2.getId());
        List<Friend> expectedFriends = List.of(friend1, friend2);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend1));
        when(friendRepository.findById(2L)).thenReturn(Optional.of(friend2));

        when(friendRepository.findAllById(friendIds)).thenReturn(expectedFriends);

        List<Friend> foundFriends = friendService.findAllFriendsById(friendIds);

        Assertions.assertNotNull(foundFriends);
        Assertions.assertEquals(expectedFriends.size(), foundFriends.size());
        Assertions.assertTrue(foundFriends.containsAll(expectedFriends));
        verify(friendRepository, times(1)).findAllById(friendIds);
    }

    @Test
    @DisplayName("findAllFriendsById throws EntityNotFoundException when any Friend is not found")
    void findAllFriendsById_ThrowEntityNotFoundException_WhenAnyFriendIsNotFound() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Set<Long> friendIds = Set.of(friend1.getId(), 2L);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend1));
        when(friendRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> friendService.findAllFriendsById(friendIds));

        Assertions.assertEquals("Amigo com ID fornecido não encontrado!", thrown.getMessage());
    }
}