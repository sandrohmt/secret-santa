package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import com.sandrohenrique.secret_santa.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        Group newGroup = new Group(data);
        saveGroup(newGroup);
        return newGroup;
    }

    public Group findGroupById(Long id) {
        return this.groupRepository.findGroupById(id).orElseThrow(RuntimeException::new); // Criar uma exceção pra isso
    }


    public Group findGroupByName(String name) {
        return this.groupRepository.findGroupByName(name).orElseThrow(RuntimeException::new); // Criar uma exceção para isso
    }

    public void addFriendById(AddFriendDTO data) {
        Optional<Group> groupOpt = groupRepository.findGroupById(data.groupId());
        Optional<Friend> friendOpt = friendRepository.findFriendById(data.friendId());

        if (groupOpt.isPresent() && friendOpt.isPresent()) { // Lançar exceção
            Group group = groupOpt.get();
            Friend friend = friendOpt.get();

            if (!group.getFriends().contains(friend)) { // Lançar exceção
                group.getFriends().add(friend);
                saveGroup(group);
            }
        }
    }

    // Encontrar amigo do grupo pelo id, entao precisa do id do grupo e do id do amigo tambem
    // Tentar adicionar varios amigos de uma vez no grupo
    // Fazer toda a regra de negocio de sortear um amigo pra cada amigo (tentar fazer com que o amigo oculto seja dinamico, que um par de pessoas nao se tire)
    // Fazer um metodo Post para a pessoa saber quem ela tirou
    // Fazer todas as exceções necessarias
    // Fazer os commits necessarios

}
