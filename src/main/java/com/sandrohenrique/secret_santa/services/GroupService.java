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

    public GroupWithFriendsDTO findGroupById(Long id) {
        Group group = this.groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        List<Friend> friends = friendService.findAllFriendsById(group.getFriendIds());

        return new GroupWithFriendsDTO(group.getId(), group.getName(), group.getEventLocation(), group.getEventDate(), group.getSpendingCap(), friends);
    }

    public GroupWithFriendsDTO findGroupByName(String name) {
        Group group = this.groupRepository.findGroupByName(name).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

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
        Optional<Group> groupOpt = groupRepository.findGroupById(data.groupId());

        if (groupOpt.isEmpty()) { // Lançar exceção
            throw new EntityNotFoundException("Grupo não encontrado!");
        }

        Group group = groupOpt.get();

        for (Long friendId : data.friendIds()) {
            friendService.findFriendById(friendId);

            if (group.getFriendIds().contains(friendId)) {
                throw new FriendAlreadyInGroupException("Amigo já está nesse grupo!");
            }
            group.getFriendIds().add(friendId);

        }

        group.setDrawn(false);
        saveGroup(group);
    }

    public void deleteFriendsInGroup(GroupFriendDTO data) {
        Group group = groupRepository.findGroupById(data.groupId()).orElseThrow();
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
        Group group = groupRepository.findGroupById(id).orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

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

// Método delete nao ta deletando mano, botar o group pra ser retornado pra verificar as deleções
// Talvez os metodos com plural devem ser feitos no singular tambem
// Talvez fazer um findGroup sem o DTO, pra repetir menos as exceções
// addFriends Precisa de pelo menos um item na lista (Nullable?)
// findByName provavelmente deve retornar mais de 1 grupo
// Não da pra pesquisar por nomes de grupo com espaço
// Acho que nao precisa de toda vez que chamar o find alguma coisa em algum metodo, lançar excecao, pq ja tem a exceçao dentro dos metodos find (testar isso depois), o problema é que o metodo dentro do service retorna um DTO nao o Group normal, isso pode dar uma dor de cabeça pra resolver
// Acho que deveria renomear o GroupFriendDTO, ta mt parecido com o GroupWithFriends
// dar uma atenção na mensagem das exceções
// fazer um redraw
// Temos um problema... se um amigo participa de dois sorteios diferentes ele nao consegue manter o drawnFriend dos dois, mantém do ultimo. talvez nao deixar o amigo participar de dois grupos ao mesmo tempo, mas quando sortear um grupo, remover o grupo, pra poder deixar amigos fazer mais de um sorteio
// update friend
// delete group
// segurança?
// testes
// Tentar fazer arquitetura limpa no EmailService