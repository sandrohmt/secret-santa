package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.AddFriendDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping(path = "{id}")
    public ResponseEntity<Group> findGroupById(@PathVariable Long id) {
        Group group = groupService.findGroupById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping(path = "createGroup")
    public ResponseEntity<Group> createGroup(@RequestBody GroupDTO group) {
        Group newGroup = groupService.createGroup(group);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PostMapping(path = "addFriend")
    public ResponseEntity<String> addFriendById(@RequestBody AddFriendDTO data) {
        groupService.addFriendById(data);
        return ResponseEntity.ok("Amigo adicionado com sucesso!");
    }
}
