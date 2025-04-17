package ch.uzh.ifi.hase.soprafs24.controller;

//Java Libraries


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.IncomingChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@Controller
public class WebSocketChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messageTemplate;
    private final UserRepository userRepository;

    WebSocketChatController(ChatService chatService, UserService userService, ChatRepository chatRepository, SimpMessagingTemplate messageTemplate, UserRepository userRepository) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.messageTemplate = messageTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/1")
    public void handleChatMessage(IncomingChatMessageDTO incomingMessage){
        //create Message object + generate additional data (MessageID, TimeStamp, Status)
        IncomingMessage sentMessage = DTOMapper.INSTANCE.convertIncomingChatMessageDTOToIncomingMessage(incomingMessage);
        Message message = chatService.CreateMessage(sentMessage);
        String original = sentMessage.getContent();
        long senderID = sentMessage.getUserId();
        User sender = userService.findByUserId(senderID);
        String senderLanguage = sender.getLanguage();
        // Api Call for Translation
        String chatId = message.getChatId();
        Chat chat = chatRepository.findById(chatId).orElse(null);
        LanguageMapping languageMap = message.getLanguageMapping();
        if (chat != null) {
            for (String language : chat.getLanguages()) { //iterate through user languages in chat directly might be better=> store all user languages in chat?
                if (languageMap.getContent(language) == null){
                    String translation = AzureAPI.AzureTranslate(original, senderLanguage, language);
                    languageMap.setContent(language, translation);
                }
                OutgoingMessage outgoingMessage = chatService.transformMessageToOutput(message, language);
                String Destination = "/topic/1/messages";
                //String Destination = "/topic/" + language + chatId;
                messageTemplate.convertAndSend(Destination, outgoingMessage);
            }
        }
        //persist Message in chat
        chatService.saveMessage(message);

        //String Destination = "/topic/"+chatId+"/messages";
        //messageTemplate.convertAndSend(Destination, message);

    }
}
