package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;


@SpringBootTest
public class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private  FriendService friendService;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("findGroupById returns a Group when successful")
    void findGroupById_ReturnsGroup_WhenSuccessful() {
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
    void findGroupById_ThrowsEntityNotFoundException_WhenGroupIsNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> groupService.findGroupById(1L));

        Assertions.assertEquals("Grupo com ID fornecido não encontrado!", thrown.getMessage());
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findGroupWIthFriendsById returns a Group when successful")
    void findGroupWIthFriendsById_ReturnsGroup_WhenSuccessful() {
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

        GroupWithFriendsDTO foundGroup = groupService.findGroupWithFriendsById(1L);

        Assertions.assertNotNull(foundGroup);
        Assertions.assertEquals(expectedGroupDTO, foundGroup);
        verify(groupRepository, times(1)).findById(1L);
        verify(friendService, times(1)).findAllFriendsById(expectedGroup.getFriendIds());
    }


}
