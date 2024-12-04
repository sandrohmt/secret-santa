package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.*;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
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
    private final FriendRepository friendRepository;
    private final EmailService emailService;


    public void saveGroup(Group group) {
        this.groupRepository.save(group);
    }

    public Group createGroup(GroupDTO data) {
        for (Long id : data.friendIds()) {
            if (friendRepository.findFriendById(id).isEmpty()) {
                throw new EntityNotFoundException("Usuário não encontrado!"); // Criar uma exceção
            }
        }
        Group newGroup = new Group(data);
        saveGroup(newGroup);
        return newGroup;
    }

    public GroupWithFriendsDTO findGroupById(Long id) {
        Group group = this.groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);
    }


    public GroupWithFriendsDTO findGroupByName(String name) {
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);

    }

    public void addFriendsById(AddFriendsDTO data) {
        Optional<Group> groupOpt = groupRepository.findGroupById(data.groupId());

        if (groupOpt.isEmpty()) { // Lançar exceção
            throw new EntityNotFoundException("Grupo não encontrado!");
        }

        Group group = groupOpt.get();

        for (Long friendId : data.friendIds()) {
            Optional<Friend> friendOpt = friendRepository.findFriendById(friendId);
            if (friendOpt.isEmpty()) {
                throw new EntityNotFoundException("Usuário não encontrado!");
            }

            if (group.getFriendIds().contains(friendId)) {
                throw new UserAlreadyInGroupException("Usuário já está nesse grupo!");
            }
            group.getFriendIds().add(friendId);

        }

        group.setDrawn(false);
        saveGroup(group);
    }

    @Transactional
    public void drawFriends(Long id) {
        Group group = groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (group.isDrawn()) {
            throw new GroupAlreadyDrawnException("Grupo já foi sorteado!");
        }

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

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

        friendRepository.saveAll(friends);

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

        Friend drawnFriend = friendRepository.findFriendById(friend.getDrawnFriendId()).get();
        String drawnFriendName = drawnFriend.getFirstName() + " " + drawnFriend.getLastName();
        List<String> drawnFriendWishlist = drawnFriend.getWishlist();

        emailService.sendEmail(groupName, eventLocation, eventDate, spendingCap, friendEmail, friendName, drawnFriendName, drawnFriendWishlist);
    }
}

// fazer um redraw
// delete friend (tem que remover do grupo tambem), sempre que o grupo mudar de algum jeito tem que colocar o isDrawn para false
// Temos um problema... se um amigo participa de dois sorteios diferentes ele nao consegue manter o drawnFriend dos dois, mantém do ultimo. talvez nao deixar o amigo participar de dois grupos ao mesmo tempo, mas quando sortear um grupo, remover o grupo, pra poder deixar amigos fazer mais de um sorteio
// update friend
// delete group
// segurança?
// testes
// Tentar fazer com lista encadeada
// talvez fazer um segundo draw pra ninguem tirar quem ja tirou no ano passado
// Tentar fazer arquitetura limpa no EmailService