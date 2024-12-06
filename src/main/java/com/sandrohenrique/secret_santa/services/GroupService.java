package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupFriendDTO;
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

    public Group findGroupById(Long id) {
        return this.groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo com ID fornecido não existe!"));
    }


    public GroupWithFriendsDTO findGroupWIthFriendsById(Long id) {
        Group group = this.groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo com ID fornecido não existe!"));

        List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);
    }

    public GroupWithFriendsDTO findGroupWithFriendsByName(String name) {
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(() -> new EntityNotFoundException("Grupo com nome fornecido não existe!"));

        List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);

    }

    public void saveGroup(Group group) {
        this.groupRepository.save(group);
    }

    public Group createGroup(GroupDTO data) {
        friendService.findAllFriendsById(data.friendIds());
        Group newGroup = new Group(data);
        saveGroup(newGroup);
        return newGroup;
    }

    public void addFriendsById(GroupFriendDTO data) {
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

    public void deleteFriendsInGroup(GroupFriendDTO data) {
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

        if (friends.size() < 2) {
            throw new InsufficientFriendsException("É necessário pelo menos 2 amigos para realizar o sorteio!");
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

        friendService.saveAllUsers(friends);

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

// Preciso escolher entre nao deixar dois grupos com o mesmo nome, ou retornar uma lista no findByname
// Um amigo só pode participar de um grupo, talvez depois que fizer o draw deletar o grupo e tirar o drawnFriendId de todos os amigos
// Talvez os metodos com plural devem ser feitos no singular tambem
// findByName provavelmente deve retornar mais de 1 grupo
// Acho que deveria renomear o GroupFriendDTO, ta mt parecido com o GroupWithFriends
// fazer um redraw
// update friend
// delete group
// segurança?
// testes
// Tentar fazer arquitetura limpa no EmailService
// Pesquisar stateful e stateless, csrf, bean e migrations