package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    @Spy
    private ChatService chatService;


    private User testUser;
    private Chat testChat;
    private Message testMessage;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("Password");
        testUser.setUsername("testUsername");
        testUser.setLanguage("en");

        testChat = new Chat();
        testChat.setChatId("1");
        HashSet<String> languages = new HashSet<>();
        languages.add("en");
        testChat.setLanguages(languages);

        testMessage = new Message();
        testMessage.setMessageId("1");
        LanguageMapping languageMapping = new LanguageMapping();
        languageMapping.setContent("en", "Hello");
        testMessage.setLanguageMapping(languageMapping);
        testMessage.setChatId(testChat.getChatId());
        testMessage.setUserId(testUser.getId());



        Mockito.when(chatRepository.save(Mockito.any())).thenReturn(testChat);
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        Mockito.when(messageRepository.save(Mockito.any())).thenReturn(testMessage);
    }

    @Test
    public void createChat_validInputs_success(){
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("Password");
        testUser2.setUsername("testUsername2");
        testUser2.setLanguage("en");

        ArrayList<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(testUser2);

        Mockito.when(userRepository.save(testUser2)).thenReturn(testUser2);
        Mockito.when(chatService.generateId()).thenReturn("1");

        Chat createdChat = chatService.createChat(users);
        Mockito.verify(chatRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testChat.getChatId(), createdChat.getChatId());
        assertEquals(testChat.getLanguages(), createdChat.getLanguages());
        assertTrue(testUser.getChats().contains(createdChat.getChatId()));
        assertTrue(testUser2.getChats().contains(createdChat.getChatId()));

    }

    @Test
    public void createChat_singleUser_throwsException(){
        ArrayList<User> users = new ArrayList<>();
        users.add(testUser);

        Mockito.when(chatService.generateId()).thenReturn("1");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.createChat(users));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void getAllMessagesWithChatId_chatExists_success(){
        testChat.setMessagesId(testMessage.getMessageId());
        ArrayList<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(testMessage);

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(testMessage);

        ArrayList<Message> actualMessages = chatService.getAllMessageWithChatId(testChat.getChatId());
        assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void getAllMessagesWithChatId_chatDoesNotExist_throwsException(){
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.getAllMessageWithChatId("InvalidChatId"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void createMessage_validInputs_success(){
        IncomingMessage incomingMessage = new IncomingMessage();
        incomingMessage.setChatId(testChat.getChatId());
        incomingMessage.setUserId(testUser.getId());
        incomingMessage.setContent("Hello");

        Mockito.when(userService.findByUserId(testUser.getId())).thenReturn(testUser);
        Mockito.when(chatService.generateId()).thenReturn("1");

        Message actualMessage = chatService.CreateMessage(incomingMessage);

        assertEquals(testMessage.getMessageId(), actualMessage.getMessageId());
        assertEquals(testMessage.getChatId(), actualMessage.getChatId());
        assertEquals(testMessage.getLanguageMapping(), actualMessage.getLanguageMapping());
    }

    /*@Test
    public void saveMessage_validInputs_success(){
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.verify(chatRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any());

    }

     */

    @Test
    public void transformMessageToOutput_Success(){
        OutgoingMessage expectedOutgoingMessage = new OutgoingMessage();
        testMessage.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Zurich")));
        expectedOutgoingMessage.setChatId(testChat.getChatId());
        expectedOutgoingMessage.setUserId(testUser.getId());
        expectedOutgoingMessage.setMessageId(testMessage.getMessageId());
        expectedOutgoingMessage.setOriginalMessage("Hello");
        expectedOutgoingMessage.setTranslatedMessage("Hello");

        Mockito.when(userService.findByUserId(Mockito.any())).thenReturn(testUser);

        OutgoingMessage actualOutgoingMessage = chatService.transformMessageToOutput(testMessage, testUser.getLanguage());
        assertEquals(expectedOutgoingMessage.getChatId(), actualOutgoingMessage.getChatId());
        assertEquals(expectedOutgoingMessage.getUserId(), actualOutgoingMessage.getUserId());
        assertEquals(expectedOutgoingMessage.getMessageId(), actualOutgoingMessage.getMessageId());
        assertEquals(expectedOutgoingMessage.getOriginalMessage(), actualOutgoingMessage.getOriginalMessage());
        assertEquals(expectedOutgoingMessage.getTranslatedMessage(), actualOutgoingMessage.getTranslatedMessage());
    }
}
