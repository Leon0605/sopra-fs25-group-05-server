package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;


import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.IncomingChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.OutgoingMessageDTO;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketChatControllerTest {

    private CompletableFuture<OutgoingMessageDTO> completableFuture;

    @Value("${local.server.port}")
    private int port;
    private String URL;

    private static final String SEND_MESSAGE_ENDPOINT = "/app/MessageHandler";
    private static final String SUBSCRIBE_MESSAGE_ENDPOINT = "/topic/";

    @MockBean
    private UserService userService;

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private ChatService chatService;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/ws";
    }

    @Test
    public void testWebSocketChat() throws ExecutionException, InterruptedException, TimeoutException {
        //Mocking Objects
        Chat testChat = new Chat();
        String chatId = UUID.randomUUID().toString();
        testChat.setChatId(chatId);

        HashSet<String> languages = new HashSet<>();
        languages.add("en");
        testChat.setLanguages(languages);

        User testUser = new User();
        Long userId = 1L;
        testUser.setId(userId);
        testUser.setLanguage("en");

        testChat.setUserIds( new ArrayList<>(Arrays.asList(userId)));

        IncomingChatMessageDTO incomingChatMessageDTO = new IncomingChatMessageDTO();
        incomingChatMessageDTO.setChatId(chatId);
        incomingChatMessageDTO.setUserId(userId);
        incomingChatMessageDTO.setContent("Hello WebSocket");

        LanguageMapping languageMapping = new LanguageMapping();
        languageMapping.setContent("en", "Hello WebSocket");

        Message testMessage = new Message();
        testMessage.setChatId(chatId);
        testMessage.setUserId(userId);
        testMessage.setMessageId(UUID.randomUUID().toString());
        testMessage.setLanguageMapping(languageMapping);
        testMessage.setTimestamp(LocalDateTime.now());

        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setChatId(chatId);
        outgoingMessage.setUserId(userId);
        outgoingMessage.setMessageId(testMessage.getMessageId());
        outgoingMessage.setOriginalMessage("Hello WebSocket");
        outgoingMessage.setTranslatedMessage("Hello WebSocket");
        outgoingMessage.setTimestamp(testMessage.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        //Expected Message handed back to Subscription endpoint after logic was applied by Controller
        OutgoingMessageDTO expectedOutgoingMessageDTO = new OutgoingMessageDTO();
        expectedOutgoingMessageDTO.setChatId(chatId);
        expectedOutgoingMessageDTO.setUserId(userId);
        expectedOutgoingMessageDTO.setMessageId(testMessage.getMessageId());
        expectedOutgoingMessageDTO.setOriginalMessage("Hello WebSocket");
        expectedOutgoingMessageDTO.setTranslatedMessage("Hello WebSocket");


        //Mocking Functions used by WebSocketChatController
        given(userService.findByUserId(Mockito.any())).willReturn(testUser);
        given(chatRepository.findById(Mockito.any())).willReturn(Optional.of(testChat));
        given(chatService.transformMessageToOutput(Mockito.any(), Mockito.any())).willReturn(outgoingMessage);
        given(chatService.CreateMessage(Mockito.any())).willReturn(testMessage);

        //Setting up a Client
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        //Setting how messages sent between Client and Server should be translated
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        //Connecting Client to Server
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {}).get(10, TimeUnit.SECONDS);
        //Client Subscribing to Subscription Endpoint of WS Controller "/topic/{language}/{chatId}
        stompSession.subscribe(SUBSCRIBE_MESSAGE_ENDPOINT + "en/" + chatId, new MessageStompFrameHandler() {});
        //Client Sending Message to Sending Endpoint of WS Controller "/app/MessageHandler"
        stompSession.send(SEND_MESSAGE_ENDPOINT, incomingChatMessageDTO);

        //Awaiting Message from Subscription Endpoint
        OutgoingMessageDTO outgoingMessageDTO = completableFuture.get(20, TimeUnit.SECONDS);

        //Testing for Expected Output
        assertNotNull(outgoingMessageDTO);
        assertEquals(expectedOutgoingMessageDTO.getChatId(), outgoingMessageDTO.getChatId());
        assertEquals(expectedOutgoingMessageDTO.getUserId(), outgoingMessageDTO.getUserId());
        assertEquals(expectedOutgoingMessageDTO.getMessageId(), outgoingMessageDTO.getMessageId());
        assertEquals(expectedOutgoingMessageDTO.getOriginalMessage(), outgoingMessageDTO.getOriginalMessage());
        assertEquals(expectedOutgoingMessageDTO.getTranslatedMessage(), outgoingMessageDTO.getTranslatedMessage());
        assertNotNull(outgoingMessageDTO.getTimestamp());
    }

    private List<Transport> createTransportClient(){
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class MessageStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders){
            System.out.println(stompHeaders.toString());
            return OutgoingMessageDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o){
            System.out.println((OutgoingMessageDTO) o);
            completableFuture.complete((OutgoingMessageDTO) o);
        }
    }
}