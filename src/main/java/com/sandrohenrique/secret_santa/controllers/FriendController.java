package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<Friend> createFriend(@RequestBody FriendDTO friend) {
        Friend newFriend = friendService.createFriend(friend);
        return new ResponseEntity<>(newFriend, HttpStatus.CREATED);
    }
}
