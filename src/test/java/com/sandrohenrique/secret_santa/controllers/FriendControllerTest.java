package com.sandrohenrique.secret_santa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandrohenrique.secret_santa.domain.Friend;
import com.sandrohenrique.secret_santa.dtos.FriendDTO;
import com.sandrohenrique.secret_santa.infra.security.TokenService;
import com.sandrohenrique.secret_santa.services.FriendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    FriendService friendService;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("createFriends create Friends when successful")
    void createFriends_CreateFriends_WhenSuccessful() throws Exception {
        FriendDTO friendDTO = new FriendDTO("Maria", "Silva", "mariasilva@gmail.com", List.of("Playstation 5", "Celular"));

        when(friendService.createFriend(friendDTO)).thenReturn(any(Friend.class));

        ResultActions response = mockMvc.perform(post("/friends/createFriends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendDTO)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}