package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
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

    public void saveGroup(Group group) {
        this.groupRepository.save(group);
    }

    public Group createGroup(GroupDTO data) {
        for (Long id : data.friendIds()) {
            if (friendRepository.findFriendById(id).isEmpty()) {
                throw new RuntimeException("Usuário não encontrado!"); // Criar uma exceção
            }
        }
        Group newGroup = new Group(data);
        saveGroup(newGroup);
        return newGroup;
    }

    public GroupWithFriendsDTO findGroupById(Long id) {
        Group group = this.groupRepository.findGroupById(id).orElseThrow(RuntimeException::new);// Criar uma exceção pra isso

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), friends);
    }


    public GroupWithFriendsDTO findGroupByName(String name) {
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(RuntimeException::new); // Criar uma exceção

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), friends);

    }

    public void addFriendsById(AddFriendsDTO data) {
        Optional<Group> groupOpt = groupRepository.findGroupById(data.groupId()); // Talvez fazer igual aos outros métodos orElseThrow

        for (Long friendId : data.friendIds()) {
            Optional<Friend> friendOpt = friendRepository.findFriendById(friendId);

            if (groupOpt.isEmpty() || friendOpt.isEmpty()) { // Lançar exceção
                throw new RuntimeException("Usuário ou grupo não encontrado!"); // Criar uma exceção
            }
            Group group = groupOpt.get();

            if (group.getFriendIds().contains(friendId)) { // Lançar exceção
                throw new RuntimeException("Usuário já está nesse grupo!"); // Criar uma exceção
            }
            group.getFriendIds().add(friendId);
            group.setDrawn(false);
            saveGroup(group);
        }
    }

    public void drawFriends(Long id) {
        Group group = groupRepository.findGroupById(id).orElseThrow(RuntimeException::new); // Criar uma exceção

        if (group.isDrawn()) {
            throw new RuntimeException("Grupo já foi sorteado!"); // Criar uma exceção
        }
        List<Long> friendIds = group.getFriendIds();

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        if (friends.size() < 2) {
            throw new RuntimeException("É necessário pelo menos 2 amigos para realizar o sorteio!"); // Criar uma exceção
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
                    break;
                }
            }

        }

        friendRepository.saveAll(friends);

        group.setDrawn(true);
        saveGroup(group);
    }
}

// Temos um problema... se um amigo participa de dois sorteios diferentes ele nao consegue manter o drawnFriend dos dois, mantém do ultimo.
// Fazer com que o sorteio seja cíclico
// Encontrar amigo do grupo pelo id, entao precisa do id do grupo e do id do amigo tambem
// Fazer um metodo Post para a pessoa saber quem ela tirou
// Fazer todas as exceções necessarias
// Lançar exceção com nome de grupo repetido
// delete update friend
// delete group
// shuffles (tentar fazer com que tal usuario só consiga ver o dele)
// talvez disparar emails
// segurança?
// testes
// Tentar fazer com lista encadeada
