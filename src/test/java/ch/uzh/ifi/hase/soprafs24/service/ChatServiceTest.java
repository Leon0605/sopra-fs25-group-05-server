package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.ReadByUsers;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.ReadByUsers;

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

    @Mock
    private ReadByUsers readByUsers;


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

        testChat.setMessagesId(testMessage.getMessageId());



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
        assertNotNull(actualMessage.getTimestamp());
    }

    /*@Test
    public void saveMessage_validInputs_success(){
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.verify(chatRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any());

    }

     */

    @Test
    public void saveMessage_invalidChatId_throwsException(){
        Message message = new Message();
        message.setChatId("InvalidChatId");
        message.setMessageId("validMessageId");

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.saveMessage(message));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    /*TODO ?
    @Test
    public void saveMessage_validInputs_Success(){
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.verify(chatRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any());
    }

     */


    @Test
    public void updateMessageStatus_invalidMessageId_throwsException(){
        Message message = new Message();
        message.setMessageId("invalidMessageId");

        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.updateMessageStatus(message.getMessageId(), testUser.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void updateMessageStatus_invalidChatId_throwsException(){
        Message message = new Message();
        message.setMessageId("validMessageId");
        message.setChatId("invalidChatId");

        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(message);
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.updateMessageStatus(message.getMessageId(), testUser.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    //Testing updateMessageStatus with a User that has already seen the message
    @Test
    public void updateMessageStatus_userReadMessage_nothingHappens(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());

        testChat.setUserIds(users);

        String originalMessageStatus = "sent";
        testMessage.setStatus(originalMessageStatus);

        ReadByUsers rBU = new ReadByUsers();
        rBU.addReadByUser(testUser.getId());
        rBU.addReadByUser(user1.getId());
        testMessage.setReadByUser(rBU);

        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(testMessage);
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);

        chatService.updateMessageStatus(testMessage.getMessageId(), testUser.getId());

        assertEquals(rBU, testMessage.getReadByUser());
        assertEquals(originalMessageStatus, testMessage.getStatus());
    }

    //testing updateMessageStatus in a Chat with 2 users and the receiver has not yet seen the message => assuring the message status is correctly updated
    @Test
    public void updateMessageStatus_singleChatUserNotReadMessage_updatesStatus(){
        User user1 = new User();
        user1.setId(2L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());

        testChat.setUserIds(users);
        ReadByUsers rBU = new ReadByUsers();
        rBU.addReadByUser(testUser.getId());

        testMessage.setReadByUser(rBU);
        testMessage.setStatus("sent");

        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(testMessage);
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);

        chatService.updateMessageStatus(testMessage.getMessageId(), user1.getId());

        assertTrue(testMessage.getReadByUser().getReadByUsers().containsAll(users));
        assertEquals("read", testMessage.getStatus());

    }
    //testing updateMessageStatus in a group chat, when a User reads a new message but not all Users in the chat have read the message yet => user is added to ReadByUsers but status remains unchanged
    @Test
    public void updateMessageStatus_groupChatNotAllRead_notUpdatesStatus(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());
        users.add(user2.getId());

        testChat.setUserIds(users);

        ReadByUsers rBU = new ReadByUsers();
        rBU.addReadByUser(testUser.getId());

        testMessage.setReadByUser(rBU);
        testMessage.setStatus("sent");

        Mockito.when(messageRepository.findByMessageId(Mockito.any())).thenReturn(testMessage);
        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);

        chatService.updateMessageStatus(testMessage.getMessageId(), user1.getId());

        assertTrue(testMessage.getReadByUser().getReadByUsers().contains(user1.getId()));
        assertEquals("sent", testMessage.getStatus());

    }

    @Test
    public void addUserToChat_invalidChat_throwsException(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());

        testChat.setUserIds(users);
        testChat.setChatId("InvalidChatId");

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.addUserToChat(testChat.getChatId(), user2.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void addUserToChat_userAlreadyInChat_doesNothing(){
        User user1 = new User();
        user1.setId(2L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());

        testChat.setUserIds(users);

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        assertDoesNotThrow(() -> chatService.addUserToChat(testChat.getChatId(), user1.getId()));
    }

    @Test
    public void addUserToChat_validInputs_success(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);
        user2.setLanguage("de");

        ArrayList<Long> users = new ArrayList<>();
        users.add(testUser.getId());
        users.add(user1.getId());

        testChat.setUserIds(users);

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.when(userService.findByUserId(user2.getId())).thenReturn(user2);

        chatService.addUserToChat(testChat.getChatId(), user2.getId());
        assertTrue(testChat.getUserIds().contains(user2.getId()));
        assertTrue(testChat.getLanguages().contains("de"));
    }

    @Test
    public void removeUserFromChat_invalidChat_throwsException(){
        User user1 = new User();
        user1.setId(2L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(user1.getId());
        users.add(testUser.getId());
        testChat.setUserIds(users);
        testChat.setChatId("invalidChatId");

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception =assertThrows(ResponseStatusException.class, () -> chatService.removeUserFromChat(testChat.getChatId(), user1.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void removeUserFromChat_userNotInChat_throwsException(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);

        ArrayList<Long> users = new ArrayList<>();
        users.add(user1.getId());
        users.add(testUser.getId());
        testChat.setUserIds(users);

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> chatService.removeUserFromChat(testChat.getChatId(), user2.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void removeUserFromChat_validInputs_success(){
        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);
        user2.setLanguage("de");

        ArrayList<Long> users = new ArrayList<>();
        users.add(user1.getId());
        users.add(testUser.getId());
        users.add(user2.getId());

        testChat.setUserIds(users);
        HashSet<String> languages = new HashSet<>();
        languages.add(testUser.getLanguage());
        languages.add(user2.getLanguage());

        Mockito.when(chatRepository.findByChatId(Mockito.any())).thenReturn(testChat);
        Mockito.when(userService.findByUserId(Mockito.any())).thenReturn(user2);

        chatService.removeUserFromChat(testChat.getChatId(), user2.getId());
        assertFalse(testChat.getUserIds().contains(user2.getId()));
        assertFalse(testChat.getLanguages().contains(user2.getLanguage()));
    }
    @Test
    public void transformMessageToOutput_Success(){
        testMessage.setOriginal("Hello");
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
