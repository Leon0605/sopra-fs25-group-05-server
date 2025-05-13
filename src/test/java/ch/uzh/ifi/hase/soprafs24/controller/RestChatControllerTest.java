package ch.uzh.ifi.hase.soprafs24.controller;



import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.UserChatDTO;

import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;

@WebMvcTest(RestChatController.class)
public class RestChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void givenUserId_whenUserExists_thenGetAllChats() throws Exception {

        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);
        ArrayList<Long> Ids = new ArrayList<>();

        Ids.add(1L);
        Ids.add(2L);

        Chat chat = new Chat();
        chat.setChatId("1");
        chat.setUserIds(Ids);

        user1.setChats("1");
        user2.setChats("1");

        given(userService.findChatWithUserId(user1.getId())).willReturn(user1.getChats());
        given(chatRepository.findByChatId(chat.getChatId())).willReturn(chat);
        MockHttpServletRequestBuilder getRequest = get("/chats").contentType(MediaType.APPLICATION_JSON).header("userId", user1.getId());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1))) //ArrayList of size 1
                .andExpect(jsonPath("$[0].chatId", is("1")))
                .andExpect(jsonPath("$[0].userIds", contains(1,2)));
    }

    @Test
    public void givenUserId_whenUserNotExists_thenNotFound() throws Exception {
        given(userService.findChatWithUserId(1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        MockHttpServletRequestBuilder getRequest = get("/chats").contentType(MediaType.APPLICATION_JSON).header("userId", 1L);

        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }
    /*
    @Test
    public void givenUserId_whenUserNoChats_thenReturnEmptyArray() throws Exception {

    }
     */

    @Test
    public void givenInputs_whenValidInputs_thenGetAllMessages() throws Exception {
        Chat chat = new Chat();
        chat.setChatId("1");
        String Token = "validToken";

        User user = new User();
        user.setId(1L);
        user.setToken(Token);
        user.setLanguage("en");

        Message message = new Message();
        message.setMessageId("1");
        message.setUserId(user.getId());
        message.setChatId(chat.getChatId());

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setChatId(chat.getChatId());
        outgoingMessage.setUserId(user.getId());
        outgoingMessage.setOriginalMessage("Hello");
        outgoingMessage.setTranslatedMessage("Hello");
        outgoingMessage.setMessageId(message.getMessageId());

        given(chatService.getAllMessageWithChatId(chat.getChatId())).willReturn(messages);
        given(chatService.transformMessageToOutput(message, user.getLanguage())).willReturn(outgoingMessage);
        given(userService.findByUserId(user.getId())).willReturn(user);
        willDoNothing().given(chatService).updateMessageStatus(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder getRequest = get("/chats/" + chat.getChatId() + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1)))
                .andExpect(jsonPath("$[0].chatId", is("1")))
                .andExpect(jsonPath("$[0].userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].messageId", is(message.getMessageId())))
                .andExpect(jsonPath("$[0].originalMessage", is("Hello")))
                .andExpect(jsonPath("$[0].translatedMessage", is("Hello")));
    }

    @Test
    public void givenInputs_whenInvalidUserId_thenNotFound() throws Exception {
        String Token = "invalidToken";
        Chat chat = new Chat();
        chat.setChatId("1");
        Message message = new Message();
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        User user = new User();
        user.setId(1L);
        given(chatService.getAllMessageWithChatId(chat.getChatId())).willReturn(messages);
        given(userService.findByUserId(user.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder getRequest = get("/chats/" + chat.getChatId() + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    public void givenInputs_whenInvalidChatId_thenNotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        Chat chat = new Chat();
        chat.setChatId("1");

        given(chatService.getAllMessageWithChatId(chat.getChatId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        MockHttpServletRequestBuilder getRequest = get("/chats/"+ chat.getChatId()+ "/" + user.getId()).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    public void createChat_whenValidInput_thenChatCreated() throws Exception {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        ArrayList<Long> Ids = new ArrayList<>();
        Ids.add(user1.getId());
        Ids.add(user2.getId());

        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);

        Chat createdChat = new Chat();
        createdChat.setChatId("1");
        createdChat.setUserIds(Ids);

        given(userService.findByUserId(user1.getId())).willReturn(user1);
        given(userService.findByUserId(user2.getId())).willReturn(user2);
        given(chatService.createChat(allUsers)).willReturn(createdChat);

        MockHttpServletRequestBuilder postRequest = post("/chats").contentType(MediaType.APPLICATION_JSON).content(asJsonString(Ids));
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    public void createChat_whenUserIdsNull_thenBadRequest() throws Exception {
        ArrayList<String> ids = null;

        MockHttpServletRequestBuilder postRequest = post("/chats").contentType(MediaType.APPLICATION_JSON).content(asJsonString(ids));
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());

    }

    @Test
    public void createChat_whenUserIdsTooSmall_thenBadRequest() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("1");

        MockHttpServletRequestBuilder postRequest = post("/chats").contentType(MediaType.APPLICATION_JSON).content(asJsonString(ids));
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void createChat_whenInvalidUserId_thenNotFound() throws Exception {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        ArrayList<Long> Ids = new ArrayList<>();
        Ids.add(user1.getId());
        Ids.add(user2.getId());

        given(userService.findByUserId(user1.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder postRequest = post("/chats").contentType(MediaType.APPLICATION_JSON).content(asJsonString(Ids));
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

}