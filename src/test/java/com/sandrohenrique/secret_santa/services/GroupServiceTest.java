package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;


@SpringBootTest
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private FriendService friendService;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("createGroup return Group when successful")
    void createGroup_ReturnGroup_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        List<Friend> friends = List.of(friend1, friend2);
        Set<Long> friendIds = Set.of(friend1.getId(), friend2.getId());

        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupDTO groupDTO = new GroupDTO("Natal em família", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, friendIds);
        Group expectedGroup = new Group(groupDTO);

        when(friendService.findAllFriendsById(friendIds)).thenReturn(friends);

        Group group = groupService.createGroup(groupDTO);

        verify(groupRepository, times(1)).save(any(Group.class));

        Assertions.assertNotNull(group);
        Assertions.assertEquals(expectedGroup, group);
        Assertions.assertEquals(friendIds, group.getFriendIds());
    }

    @Test
    @DisplayName("createGroup throws EntityNotFoundException when Friend id is not found")
    void createGroup_ThrowEntityNotFoundException_WhenFriendIdIsNotFound() {
        Set<Long> ids = Set.of(1L, 2L);

        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        GroupDTO groupDTO = new GroupDTO("Natal em família", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, ids);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5"), null)));
        when(friendRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> groupService.createGroup(groupDTO));

        Assertions.assertEquals("Amigo com ID fornecido não encontrado!", thrown.getMessage());

        verify(groupRepository, never()).save(any(Group.class));
    } // Consertar esse teste, talvez tenha que mudar a implementação do createGroup.


    @Test
    @DisplayName("findGroupById returns a Group when successful")
    void findGroupById_ReturnGroup_WhenSuccessful() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L,3L), false);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(expectedGroup));

        Group foundGroup = groupService.findGroupById(1L);

        Assertions.assertNotNull(foundGroup);
        Assertions.assertEquals(expectedGroup, foundGroup);
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findGroupById throws EntityNotFoundException when a Group is not found")
    void findGroupById_ThrowEntityNotFoundException_WhenGroupIsNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> groupService.findGroupById(1L));

        Assertions.assertEquals("Grupo com ID fornecido não encontrado!", thrown.getMessage());
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findGroupWIthFriendsById returns a Group when successful")
    void findGroupWIthFriendsById_ReturnGroup_WhenSuccessful() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L), false);

        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        List<Friend> friends = List.of(friend1, friend2);
        GroupWithFriendsDTO expectedGroupDTO = new GroupWithFriendsDTO(
                expectedGroup.getId(),
                expectedGroup.getName(),
                expectedGroup.getEventLocation(),
                expectedGroup.getEventDate(),
                expectedGroup.getSpendingCap(),
                friends
        );

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(expectedGroup));
        when(friendService.findAllFriendsById(expectedGroup.getFriendIds())).thenReturn(friends);

        GroupWithFriendsDTO foundGroup = groupService.findGroupWithFriendsById(groupId);

        Assertions.assertNotNull(foundGroup);
        Assertions.assertEquals(expectedGroupDTO, foundGroup);
        verify(groupRepository, times(1)).findById(groupId);
        verify(friendService, times(1)).findAllFriendsById(expectedGroup.getFriendIds());
    }

    @Test
    @DisplayName("findGroupWIthFriendsById throws EntityNotFoundException when a Group is not found")
    void findGroupWIthFriendsById_ThrowEntityNotFoundException_WhenGroupIsNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> groupService.findGroupWithFriendsById(1L));

        Assertions.assertEquals("Grupo com ID fornecido não encontrado!", thrown.getMessage());
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findGroupWIthFriendsByName returns a List of Group when successful")
    void findGroupWIthFriendsByName_ReturnListOfGroup_WhenSuccessful() {
        String groupName = "Amigo Secreto de Fim de Ano";
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group expectedGroup = new Group(1L, groupName, "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L,2L), false);

        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        List<Friend> friends = List.of(friend1, friend2);
        GroupWithFriendsDTO expectedGroupDTO = new GroupWithFriendsDTO(
                expectedGroup.getId(),
                expectedGroup.getName(),
                expectedGroup.getEventLocation(),
                expectedGroup.getEventDate(),
                expectedGroup.getSpendingCap(),
                friends
        );

        when(groupRepository.findByName(groupName)).thenReturn(List.of(expectedGroup));
        when(friendService.findAllFriendsById(expectedGroup.getFriendIds())).thenReturn(friends);

        List<GroupWithFriendsDTO> foundGroups = groupService.findGroupWithFriendsByName(groupName);

        Assertions.assertNotNull(foundGroups);
        Assertions.assertEquals(expectedGroupDTO, foundGroups.get(0));
        verify(groupRepository, times(1)).findByName(groupName);
        verify(friendService, times(1)).findAllFriendsById(expectedGroup.getFriendIds());
    }

    @Test
    @DisplayName("findGroupWIthFriendsByName throws EntityNotFoundException when no Group is found")
    void findGroupWIthFriendsByName_ThrowEntityNotFoundException_WhenNoGroupIsFound() {
        String groupName = "Amigo Secreto de Fim de Ano";
        when(groupRepository.findByName(groupName)).thenReturn(Collections.emptyList());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> groupService.findGroupWithFriendsByName(groupName));

        Assertions.assertEquals("Grupo com nome fornecido não encontrado!", thrown.getMessage());
        verify(groupRepository, times(1)).findByName(groupName);
    }

    @Test
    @DisplayName("addFriendsById add friends to group when successful")
    void addFriendsById_AddFriendsToGroup_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of(1L, 2L));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(friendRepository.findById(friend1.getId())).thenReturn(Optional.of(friend1));
        when(friendRepository.findById(friend2.getId())).thenReturn(Optional.of(friend2));

        groupService.addFriendsById(data);

        Assertions.assertTrue(group.getFriendIds().containsAll(Set.of(1L,2L)));
        Assertions.assertFalse(group.isDrawn());

        verify(groupRepository, times(1)).save(group);
    }
}
