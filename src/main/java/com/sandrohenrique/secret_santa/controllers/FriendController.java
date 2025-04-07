package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friends", description = "Endpoints for managing users participating in the Secret Santa group.")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @Operation(summary = "List all friends", description = "Returns a list of all friends registered in the Secret Santa system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of friends.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public ResponseEntity<List<Friend>> getAllFriends() {
        List<Friend> friends = friendService.getAllFriends();
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @PostMapping(path = "createFriend")
    @Operation(summary = "Create a new friend", description = "Register a new friend in the Secret Santa system using the provided data.")
    @ApiResponse(responseCode = "201", description = "Successfully created a new friend.")
    @ApiResponse(responseCode = "409", description = "A friend with the provided email is already registered.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public ResponseEntity<Friend> createFriend(@RequestBody FriendDTO friend) {
        Friend newFriend = friendService.createFriend(friend);
        return new ResponseEntity<>(newFriend, HttpStatus.CREATED);
    }

    @PostMapping(path = "createFriends")
    @Operation(summary = "Create multiple friends", description = "Registers multiple friends in the Secret Santa system using the provided data in a single request.")
    @ApiResponse(responseCode = "201", description = "Successfully created all friends.")
    @ApiResponse(responseCode = "409", description = "A friend with the provided email is already registered.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public ResponseEntity<List<FriendDTO>> createFriends(@RequestBody List<FriendDTO> friends) {
        for (FriendDTO friend: friends) {
            friendService.createFriend(friend);
        }
        return new ResponseEntity<>(friends, HttpStatus.CREATED);
    }
}
