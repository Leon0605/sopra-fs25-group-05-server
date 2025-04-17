package ch.uzh.ifi.hase.soprafs24.controller;

//Java Libraries


import ch.uzh.ifi.hase.soprafs24.rest.dto.IncomingChatMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.entity.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

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

    @MessageMapping("/MessageHandler")
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
                //String Destination = "/topic/1/messages";

                String Destination =  "/topic/" + language +"/"+ chatId;
                messageTemplate.convertAndSend(Destination, outgoingMessage);
            }
        }
        //persist Message in chat
        chatService.saveMessage(message);

        //String Destination = "/topic/"+chatId+"/messages";
        //messageTemplate.convertAndSend(Destination, message);

    }
}
