package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.exceptions.UserAlreadyInGroupException;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public void saveUser(Friend friend) {
        this.friendRepository.save(friend);
    }

    public Friend createFriend(FriendDTO data) {
        Friend newFriend = new Friend(data);
        List<Friend> friends = getAllFriends(); // Testar isso em casa
        for (Friend friend : friends) {
            if (newFriend.getEmail().equals(friend.getEmail())) {
                throw new UserAlreadyInGroupException("Usuário já cadastrado com esse email!"); // Talvez fazer isso de outra forma, porque ele precisa percorrer a lista inteira pra fazer essa verificação
            }
        }
        saveUser(newFriend);
        return newFriend;
    }

    public List<Friend> getAllFriends() {
        return this.friendRepository.findAll(); // Testar isso em casa
    }
}
