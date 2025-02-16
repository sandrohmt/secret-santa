package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.*;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final FriendService friendService;
    private final EmailService emailService;

    public void saveGroup(Group group) {
        this.groupRepository.save(group);
    }

    public Group createGroup(GroupDTO data) {
        friendService.findAllFriendsById(data.friendIds());
        Group newGroup = new Group(data);
        saveGroup(newGroup);
        return newGroup;
    }

    public Group findGroupById(Long id) {
        return this.groupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grupo com ID fornecido não encontrado!"));
    }


    public GroupWithFriendsDTO findGroupWithFriendsById(Long id) {
        Group group = this.groupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grupo com ID fornecido não encontrado!"));

        List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);
    }

    public List<GroupWithFriendsDTO> findGroupWithFriendsByName(String name) {
        List<Group> groups = this.groupRepository.findByName(name);

        if (groups.equals(Collections.emptyList())) {
            throw new EntityNotFoundException("Grupo com nome fornecido não encontrado!");
        }

        List<GroupWithFriendsDTO> groupWithFriendsDTOList = new ArrayList<>();

        for (Group group: groups) {
            List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());
            GroupWithFriendsDTO dto = new GroupWithFriendsDTO(
                    group.getId(),
                    group.getName(),
                    group.getEventLocation(),
                    group.getEventDate(),
                    group.getSpendingCap(),
                    friends
            );
            groupWithFriendsDTOList.add(dto);
        }

        return groupWithFriendsDTOList;
    }

    public void addFriendsById(GroupFriendIdsDTO data) {
        Group group = findGroupById(data.groupId());

        if (data.friendIds().isEmpty()) {
            throw new InsufficientFriendsException("Adicione pelo menos um amigo!");
        }

        for (Long friendId : data.friendIds()) {
            friendService.findFriendById(friendId);

            if (group.getFriendIds().contains(friendId)) {
                throw new FriendAlreadyInGroupException("Amigo já pertence a esse grupo!");
            }
            group.getFriendIds().add(friendId);

        }

        group.setDrawn(false);
        saveGroup(group);
    }

    public void deleteFriendsInGroup(GroupFriendIdsDTO data) {
        Group group = findGroupById(data.groupId()); // olhar depois
        Set<Long> idsToBeDeleted = data.friendIds();
        for (Long id: idsToBeDeleted) {
            if (!group.getFriendIds().contains(id)) {
                throw new FriendNotInGroupException("O amigo com o ID fornecido não faz parte do grupo especificado.");
            }
            group.getFriendIds().remove(id);
            saveGroup(group);
        }
    }

    @Transactional
    public void drawFriends(Long id) {
        Group group = findGroupById(id);

        if (group.isDrawn()) {
            throw new GroupAlreadyDrawnException("Grupo já foi sorteado!");
        }

        List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());

        if (friends.size() < 3) {
            throw new InsufficientFriendsException("É necessário pelo menos 3 amigos para realizar o sorteio!");
        }

        LinkedList<Friend> shuffledFriends = new LinkedList<>(friends);
        Collections.shuffle(shuffledFriends);

        for (int i = 0; i < shuffledFriends.size(); i++) {
            Friend current = shuffledFriends.get(i);
            Friend next = (i + 1 < shuffledFriends.size())
                    ? shuffledFriends.get(i + 1)
                    : shuffledFriends.getFirst();
            current.setDrawnFriendId(next.getId());
            sendEmailsToFriends(group, current);
        }

        friendService.saveAllFriends(friends);

        group.setDrawn(true);
        saveGroup(group);
    }

    public void sendEmailsToFriends(Group group, Friend friend) {
        String groupName = group.getName();
        String eventLocation = group.getEventLocation();
        String eventDate = group.getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Float spendingCap = group.getSpendingCap();

        String friendName = friend.getFirstName() + " " + friend.getLastName();
        String friendEmail = friend.getEmail();

        Friend drawnFriend = friendService.findFriendById(friend.getDrawnFriendId());
        String drawnFriendName = drawnFriend.getFirstName() + " " + drawnFriend.getLastName();
        List<String> drawnFriendWishlist = drawnFriend.getWishlist();

        emailService.sendEmail(groupName, eventLocation, eventDate, spendingCap, friendEmail, friendName, drawnFriendName, drawnFriendWishlist);
    }
}

// Criar um Service compartilhado e colocar os métodos de GroupService que injetam FriendService e colocar o deleteFriends la, ou só colocar o metodo de deleteFriend mesmo, e fazer os testes desse service novo
// delete group
// documentar com o swagger, procurar o github do MurilloMarquesSantos para essas e mais coisas
// Pesquisar stateful e stateless, csrf, bean e migrations, rever o video de spring security da Fernanda Kipper
// Postar o projeto no github com readme tudo bonitinho
