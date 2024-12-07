package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.dtos.GroupFriendDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.exceptions.FriendAlreadyInGroupException;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
//    private final GroupService groupService; Provavelmente mandar remover o amigo no groupService

    public void saveAllUsers(List<Friend> friends) {
        this.friendRepository.saveAll(friends);
    }

    public void saveUser(Friend friend) {
        this.friendRepository.save(friend);
    }

    public List<Friend> getAllFriends() {
        return this.friendRepository.findAll();
    }

    public Friend createFriend(FriendDTO data) {
        Friend newFriend = new Friend(data);
            if (this.friendRepository.findByEmail(newFriend.getEmail()).isPresent()) {
                throw new FriendAlreadyInGroupException("Usuário já cadastrado com esse email!");
            }
        saveUser(newFriend);
        return newFriend;
    }

    public Friend findFriendById(Long id) {
        return this.friendRepository.findFriendById(id).orElseThrow(() -> new EntityNotFoundException("Amigo com ID fornecido não encontrado!"));
    }

    public List<Friend> findAllFriendsById(Set<Long> friends) {
        for (Long id: friends) {
            findFriendById(id);
        }

        return friendRepository.findAllById(friends);
    }

//    public void deleteFriends(GroupFriendDTO data) {
//        groupService.deleteFriendsInGroup(data);
//        for (Long friendId: data.friendIds()) {
//            this.friendRepository.deleteAll(friendRepository.findFriendById(friendId).get());
//        }
//    }

}
