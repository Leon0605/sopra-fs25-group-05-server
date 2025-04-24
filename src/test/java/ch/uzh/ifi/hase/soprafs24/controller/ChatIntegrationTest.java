package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    public void setup(){
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    public void getAllChats_validUser_success() throws Exception {
        User testUser = new User();
        testUser.setPassword("Password");
        testUser.setUsername("Username");
        testUser.setToken("Token");
        testUser.setStatus(UserStatus.ONLINE);

        Chat chat1 = new Chat();
        chat1.setChatId("1");
        Chat chat2 = new Chat();
        chat2.setChatId("2");

        chatRepository.save(chat1);
        chatRepository.save(chat2);

        testUser.setChats(chat1.getChatId());
        testUser.setChats(chat2.getChatId());
        userRepository.save(testUser);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/chats").contentType(MediaType.APPLICATION_JSON).header("userId", testUser.getId());
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].chatId").value(chat1.getChatId()))
                .andExpect(jsonPath("$[1].chatId").value(chat2.getChatId()));
    }
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
