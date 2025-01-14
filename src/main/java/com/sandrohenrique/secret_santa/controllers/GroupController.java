package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping(path = "by-id/{id}")
    public ResponseEntity<GroupWithFriendsDTO> findGroupById(@PathVariable Long id) { // Mostrar o amigo sorteado tambem
        GroupWithFriendsDTO group = groupService.findGroupWithFriendsById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping(path = "by-name/{name}")
    public ResponseEntity<List<GroupWithFriendsDTO>> findGroupByName(@PathVariable String name) {
        List<GroupWithFriendsDTO> groups = groupService.findGroupWithFriendsByName(name);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PostMapping(path = "createGroup")
    public ResponseEntity<Group> createGroup(@RequestBody GroupDTO group) {
        Group newGroup = groupService.createGroup(group);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PostMapping(path = "addFriends")
    public ResponseEntity<String> addFriendsById(@RequestBody GroupFriendIdsDTO data) {
        groupService.addFriendsById(data);
        return ResponseEntity.ok("Amigos adicionados com sucesso!");
    }

    @PostMapping(path = "draw/{id}")
    public ResponseEntity<GroupWithFriendsDTO> drawFriends(@PathVariable Long id) {
        GroupWithFriendsDTO group = groupService.findGroupWithFriendsById(id);
        groupService.drawFriends(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFriendsInGroup(@RequestBody GroupFriendIdsDTO data) {
        groupService.deleteFriendsInGroup(data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
