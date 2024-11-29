package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(RuntimeException::new);// Criar uma exceção pra isso

        List<Friend> friends = friendRepository.findAllById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), friends);

    }

    public void addFriendById(AddFriendDTO data) {
        Optional<Group> groupOpt = groupRepository.findGroupById(data.groupId());
        Optional<Friend> friendOpt = friendRepository.findFriendById(data.friendId());

        if (groupOpt.isEmpty() || friendOpt.isEmpty()) { // Lançar exceção
            throw new RuntimeException("Usuário ou grupo não encontrado!"); // Criar uma exceção
        }
        Group group = groupOpt.get();
        Long friendId = friendOpt.get().getId();

        if (group.getFriendIds().contains(friendId)) { // Lançar exceção
            throw new RuntimeException("Usuário já está nesse grupo!"); // Criar uma exceção
        }
        group.getFriendIds().add(friendId);
        saveGroup(group);

        // Encontrar amigo do grupo pelo id, entao precisa do id do grupo e do id do amigo tambem
        // Tentar adicionar varios amigos de uma vez no grupo
        // Fazer toda a regra de negocio de sortear um amigo pra cada amigo (tentar fazer com que o amigo oculto seja dinamico, que um par de pessoas nao se tire)
        // Fazer um metodo Post para a pessoa saber quem ela tirou
        // Fazer todas as exceções necessarias
        // Lançar exceção com nome de grupo repetido
        // delete update friend
        // delete group
        // commits
        // shuffles (tentar faze4r com que tal usuario só consiga ver o dele
        // talvez disparar emails
        // segurança?
        // testes
    }
}
