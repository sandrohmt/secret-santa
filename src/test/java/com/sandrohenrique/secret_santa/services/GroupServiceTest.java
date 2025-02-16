package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.*;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Test
    @DisplayName("addFriendsById throws InsufficientFriendsException when no Friends are added")
    void addFriendsById_ThrowInsufficientFriendsException_WhenNoFriendsAreAdded() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(friendRepository.findById(1L)).thenReturn(Optional.empty());
        when(friendRepository.findById(2L)).thenReturn(Optional.empty());

        InsufficientFriendsException thrown = Assertions.assertThrows(InsufficientFriendsException.class, () -> groupService.addFriendsById(data));

        Assertions.assertEquals("Adicione pelo menos um amigo!" , thrown.getMessage());

        verify(groupRepository, never()).save(group);
    }

    @Test
    @DisplayName("addFriendsById throws FriendAlreadyInGroupException when Friend is already in the group")
    void addFriendsById_ThrowFriendAlreadyInGroupException_WhenFriendIsAlreadyInTheGroup() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L, 2L), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of(friend1.getId(), friend2.getId()));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(friendRepository.findById(friend1.getId())).thenReturn(Optional.of(friend1));
        when(friendRepository.findById(friend2.getId())).thenReturn(Optional.of(friend2));

        FriendAlreadyInGroupException thrown = Assertions.assertThrows(FriendAlreadyInGroupException.class, () -> groupService.addFriendsById(data));

        Assertions.assertEquals("Amigo já pertence a esse grupo!" , thrown.getMessage());

        Assertions.assertEquals(Set.of(1L, 2L), group.getFriendIds());

        verify(groupRepository, never()).save(group);
    }

    @Test
    @DisplayName("deleteFriendsInGroup delete Friends from Group when successful")
    void deleteFriendsInGroup_DeleteFriendsFromGroup_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, new HashSet<>(Set.of(1L, 2L)), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of(friend1.getId(), friend2.getId()));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        groupService.deleteFriendsInGroup(data);

        Assertions.assertTrue(group.getFriendIds().isEmpty());

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(2)).save(group);
    }

    @Test
    @DisplayName("deleteFriendsInGroup throws FriendNotInGroupException when Friend is not in Group")
    void deleteFriendsInGroup_ThrowFriendNotInGroupException_WhenFriendIsNotInGroup() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(), false);

        GroupFriendIdsDTO data = new GroupFriendIdsDTO(groupId, Set.of(1L));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        FriendNotInGroupException thrown = Assertions.assertThrows(FriendNotInGroupException.class, () -> groupService.deleteFriendsInGroup(data));

        Assertions.assertEquals("O amigo com o ID fornecido não faz parte do grupo especificado." , thrown.getMessage());

        Assertions.assertTrue(group.getFriendIds().isEmpty());

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, never()).save(group);
    }

    @Test
    @DisplayName("drawFriends draw Friends when successful")
    void drawFriends_DrawFriends_WhenSuccessful() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        Friend friend3 = new Friend(3L, "Arthur", "Oliveira", "arthuroliveira@gmail.com", List.of("Kindle", "Livro"), null);
        List<Friend> friends = List.of(friend1, friend2, friend3);
        Set<Long> friendIds = Set.of(friend1.getId(), friend2.getId(), friend3.getId());

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L, 2L, 3L), false);


        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(friendService.findAllFriendsById(friendIds)).thenReturn(friends);
        when(friendService.findFriendById(1L)).thenReturn(friend3);
        when(friendService.findFriendById(2L)).thenReturn(friend1);
        when(friendService.findFriendById(3L)).thenReturn(friend2);

        groupService.drawFriends(groupId);

        verify(groupRepository, times(1)).findById(groupId);
        verify(friendService, times(1)).saveAllFriends(friends);
        verify(groupRepository, times(1)).save(group);

        Assertions.assertTrue(group.isDrawn());
        Assertions.assertEquals(friend1.getId(), friend3.getDrawnFriendId());
        Assertions.assertEquals(friend2.getId(), friend1.getDrawnFriendId());
        Assertions.assertEquals(friend3.getId(), friend2.getDrawnFriendId());
    }

    @Test
    @DisplayName("drawFriends throws GroupAlreadyDrawnException when Group was already drawn")
    void drawFriends_ThrowGroupAlreadyDrawnException_WhenGroupWasAlreadyDrawn() {
        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L, 2L, 3L), true);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        GroupAlreadyDrawnException thrown = Assertions.assertThrows(GroupAlreadyDrawnException.class, () -> groupService.drawFriends(groupId));

        Assertions.assertEquals("Grupo já foi sorteado!" , thrown.getMessage());

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, never()).save(group);

        Assertions.assertTrue(group.isDrawn());
    }

    @Test
    @DisplayName("drawFriends throws InsufficientFriendsException when Group has less than 3 friends")
    void drawFriends_ThrowInsufficientFriendsException_WhenGroupHasLessThan3Friends() {
        Friend friend1 = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend friend2 = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);
        List<Friend> friends = List.of(friend1, friend2);
        Set<Long> friendIds = Set.of(friend1.getId(), friend2.getId());

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L, 2L), false);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(friendService.findAllFriendsById(friendIds)).thenReturn(friends);

        InsufficientFriendsException thrown = Assertions.assertThrows(InsufficientFriendsException.class, () -> groupService.drawFriends(groupId));

        Assertions.assertEquals("É necessário pelo menos 3 amigos para realizar o sorteio!" , thrown.getMessage());


        verify(groupRepository, times(1)).findById(groupId);
        verify(friendService, never()).saveAllFriends(friends);
        verify(groupRepository, never()).save(group);

        Assertions.assertFalse(group.isDrawn());
    }

    @ParameterizedTest
    @CsvSource({
            "addFriendsById",
            "deleteFriendsInGroup",
            "drawFriends"
    })
    @DisplayName("Methods throws EntityNotFoundException when a Group is not found")
    void methods_ThrowEntityNotFoundException_WhenGroupIsNotFound(String methodName) {
        Long groupId = 1L;
        GroupFriendIdsDTO groupFriendIdsDTO = new GroupFriendIdsDTO(groupId, Set.of(1L));
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            switch (methodName) {
                case "addFriendsById":
                    groupService.addFriendsById(groupFriendIdsDTO);
                    break;
                case "deleteFriendsInGroup":
                    groupService.deleteFriendsInGroup(groupFriendIdsDTO);
                    break;
                case "drawFriends":
                    groupService.drawFriends(groupId);
                    break;
            default:
                throw new IllegalArgumentException("Método inválido: " + methodName);
            }
        });

        Assertions.assertEquals("Grupo com ID fornecido não encontrado!" , thrown.getMessage());


        verify(groupRepository, times(1)).findById(groupId);
        verifyNoMoreInteractions(groupRepository);
    }

    @Test
    @DisplayName("sendEmailsToFriends sends emails to each Friend")
    void sendEmailsToFriends_SendEmailsToEachFriend_WhenSuccessful() {
        Friend friend = new Friend(1L, "Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"), null);
        Friend drawnFriend = new Friend(2L, "José", "Souza", "josesouza@gmail.com", List.of("Tablet", "Piano"), null);

        Long groupId = 1L;
        LocalDate eventDate = LocalDate.of(2024, 12, 20);
        Group group = new Group(groupId, "Amigo Secreto de Fim de Ano", "Rua das Flores, 123 - Salão de Festas", eventDate, 100F, Set.of(1L, 2L), false);

        when(friendService.findFriendById(friend.getDrawnFriendId())).thenReturn(drawnFriend);

        groupService.sendEmailsToFriends(group, friend);

        verify(emailService, times(1)).sendEmail(
                eq(group.getName()),
                eq(group.getEventLocation()),
                eq(group.getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                eq(group.getSpendingCap()),
                eq(friend.getEmail()),
                eq(friend.getFirstName() + " " + friend.getLastName()),
                eq(drawnFriend.getFirstName() + " " + drawnFriend.getLastName()),
                eq(drawnFriend.getWishlist())
        );
    }
}
