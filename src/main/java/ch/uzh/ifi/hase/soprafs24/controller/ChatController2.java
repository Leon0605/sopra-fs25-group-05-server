package ch.uzh.ifi.hase.soprafs24.controller;

//Java Libraries
import java.util.ArrayList;

//Spring Libraries
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//internal Classes
import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessageContentDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserChatDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
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

    @MessageMapping("/app/MessageReceiver")
    public void handleChatMessage(ChatMessageDTO incomingMessage){
        Message message = DTOMapper.INSTANCE.convertMessageDTOToMessageEntity(incomingMessage);
        String chatID = message.getChatId();
        //String content = message.getContent();
        //message persistance
        chatService.saveMessage(message);
        String Destination = "/topic/"+chatID+"/messages";
        messageTemplate.convertAndSend(Destination, message.getContent());
    }

}
