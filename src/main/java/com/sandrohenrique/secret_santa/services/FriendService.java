package com.sandrohenrique.secret_santa.services;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.repositories.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public void saveUser(Friend friend) {
        this.friendRepository.save(friend);
    }

    public Friend createFriend(FriendDTO data) {
        Friend newFriend = new Friend(data);
        saveUser(newFriend);
        return newFriend;
    }

}
