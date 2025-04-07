package com.sandrohenrique.secret_santa.controllers;

import com.sandrohenrique.secret_santa.domain.Group;
import com.sandrohenrique.secret_santa.dtos.GroupFriendIdsDTO;
import com.sandrohenrique.secret_santa.dtos.GroupDTO;
import com.sandrohenrique.secret_santa.dtos.GroupWithFriendsDTO;
import com.sandrohenrique.secret_santa.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Endpoints for managing groups participating in the Secret Santa system.")
public class GroupController {

    private final GroupService groupService;

    @GetMapping(path = "by-id/{id}")
    @Operation(summary = "Get group by Id", description = "Returns a group with the provided Id, including its members.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the group.")
    @ApiResponse(responseCode = "404", description = "A group with the provided Id was not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while retrieving the group.")
    public ResponseEntity<GroupWithFriendsDTO> findGroupById(@PathVariable Long id) { // Mostrar o amigo sorteado tambem
        GroupWithFriendsDTO group = groupService.findGroupWithFriendsById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping(path = "by-name/{name}")
    @Operation(summary = "List groups by name", description = "Returns a list of groups with the provided name, including its members.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the groups.")
    @ApiResponse(responseCode = "404", description = "No groups with the provided name were found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while retrieving the groups.")
    public ResponseEntity<List<GroupWithFriendsDTO>> findGroupByName(@PathVariable String name) {
        List<GroupWithFriendsDTO> groups = groupService.findGroupWithFriendsByName(name);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PostMapping(path = "createGroup")
    @Operation(summary = "Create a new group", description = "Register a new group in the Secret Santa system using the provided data.")
    @ApiResponse(responseCode = "201", description = "Successfully created a new group.")
    @ApiResponse(responseCode = "404", description = "Friend with the provided Id was not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while creating the new group.")
    public ResponseEntity<Group> createGroup(@RequestBody GroupDTO group) {
        Group newGroup = groupService.createGroup(group);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PostMapping(path = "addFriends")
    @Operation(summary = "Add friends to a group", description =  "Adds a list of existing friends to the specified Secret Santa group. ")
    @ApiResponse(responseCode = "200", description = "Friends successfully added to the group.")
    @ApiResponse(responseCode = "404", description = "Group or one of the provided friend Ids was not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while adding friends to the group.")
    public ResponseEntity<String> addFriendsById(@RequestBody GroupFriendIdsDTO data) {
        groupService.addFriendsById(data);
        return ResponseEntity.ok("Amigos adicionados com sucesso!");
    }

    @PostMapping(path = "draw/{id}")
    @Operation(summary = "Perform the Secret Santa draw for a group", description =  "Randomly assigns each friend in the specified group another friend to gift, following Secret Santa rules (no self-assignment).")
    @ApiResponse(responseCode = "200", description = "Groups successfully drawn.")
    @ApiResponse(responseCode = "404", description = "A group with the provided Id was not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while drawing the group.")
    public ResponseEntity<GroupWithFriendsDTO> drawFriends(@PathVariable Long id) {
        groupService.drawFriends(id);
        GroupWithFriendsDTO group = groupService.findGroupWithFriendsById(id); // Ver se vai dar certo, se nao colocar como primeira linha novamente
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete friends from a group", description =  "Delete one or more friends from the specified group in the Secret Santa system.")
    @ApiResponse(responseCode = "204", description = "Friends successfully removed from the group..")
    @ApiResponse(responseCode = "404", description = "Group or one of the provided friend Ids was not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected server error while deleting friends from the group.")
    public ResponseEntity<Void> deleteFriendsInGroup(@RequestBody GroupFriendIdsDTO data) {
        groupService.deleteFriendsInGroup(data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
