package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.exceptions.*;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        return new GroupWithFriendsDTO(group.getId(), group.getName(), friends);
    }


    public GroupWithFriendsDTO findGroupByName(String name) {
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), friends);

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

    public void drawFriends(Long id) {
        Group group = groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (group.isDrawn()) {
            throw new GroupAlreadyDrawnException("Grupo já foi sorteado!");
        }
        Set<Long> friendIds = group.getFriendIds();

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        if (friends.size() < 2) {
            throw new InsufficientFriendsException("É necessário pelo menos 2 amigos para realizar o sorteio!");
        }

        LinkedList<Long> availableFriends = new LinkedList<>(friendIds);
        Collections.shuffle(availableFriends);


        for (Friend friend : friends) {
            Iterator<Long> iterator = availableFriends.iterator();
            while(iterator.hasNext()) {
                Long drawnId = iterator.next();
                if (!drawnId.equals(friend.getId())) {
                    friend.setDrawnFriendId(drawnId);
                    iterator.remove();
                    String friendName = friend.getFirstName() + " " + friend.getLastName();
                    Friend drawnFriend = friendRepository.findFriendById(friend.getDrawnFriendId()).get();
                    String drawnFriendName = drawnFriend.getFirstName() + " " + drawnFriend.getLastName();
                    List<String> drawnFriendWishlist = drawnFriend.getWishlist();
                    emailService.sendTextEmail(group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friend.getEmail(), friendName, drawnFriendName, drawnFriendWishlist); // deixar essa mensagem mais bonita. provavelmente fazer isso no emailService, nao aqui
                    break;
                }
            }
        }

        friendRepository.saveAll(friends);

        group.setDrawn(true);
        saveGroup(group);

    }
}

// desacoplar o envio de emails do drawFriends, ou sei la, ver algo no gpt pra resolver esse problema de varios parametros
// delete friend (tem que remover do grupo tambem)
// Temos um problema... se um amigo participa de dois sorteios diferentes ele nao consegue manter o drawnFriend dos dois, mantém do ultimo. talvez nao deixar o amigo participar de dois grupos ao mesmo tempo, mas quando sortear um grupo, remover o grupo, pra poder deixar amigos fazer mais de um sorteio
// Fazer com que o sorteio seja cíclico
// Encontrar amigo do grupo pelo id, entao precisa do id do grupo e do id do amigo tambem
// Fazer um metodo Post para a pessoa saber quem ela tirou
// update friend
// delete group
// shuffles (tentar fazer com que tal usuario só consiga ver o dele)
// segurança?
// testes
// Tentar fazer com lista encadeada
// Fazer um segundo draw pra ninguem tirar quem ja tirou no ano passado