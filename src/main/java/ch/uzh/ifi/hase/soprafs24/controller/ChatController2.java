package ch.uzh.ifi.hase.soprafs24.controller;

//Java Libraries
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@Controller
public class ChatController2 {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messageTemplate;
    ChatController2(ChatService chatService,UserService userService,ChatRepository chatRepository, SimpMessagingTemplate messageTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.messageTemplate = messageTemplate;
    }

  /*   @MessageMapping("/app/{chatId}")
    public void handleChatMessage(ChatMessageDTO incomingMessage){
        Message message = DTOMapper.INSTANCE.convertMessageDTOToMessageEntity(incomingMessage);
        String chatID = message.getChatId();
        //String content = message.getContent();
        //message persistance
        chatService.saveMessage(message);
        String Destination = "/topic/"+chatID+"/messages";
        messageTemplate.convertAndSend(Destination, message.getContent());
    }
*/
}
