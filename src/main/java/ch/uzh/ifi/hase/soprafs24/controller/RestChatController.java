package ch.uzh.ifi.hase.soprafs24.controller;

//Java Libraries
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.OutgoingMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.UserChatDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@RestController
public class RestChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    RestChatController(ChatService chatService, UserService userService, ChatRepository chatRepository, UserRepository userRepository) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/chats")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<UserChatDTO> getAllChats(@RequestHeader Long userId) {

        ArrayList<String> chatsId = userService.findChatWithUserId(userId);

        ArrayList<UserChatDTO> userChatDTOs = new ArrayList<>();

        for (String chatId : chatsId) {
            userChatDTOs.add(DTOMapper.INSTANCE.convertChatEntityToUserChatDTO(chatRepository.findByChatId(chatId)));
        }
        return userChatDTOs;
    }


    @PutMapping("/{messageId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody

    public void updateMessageStatus(@PathVariable String messageId, @PathVariable Long userId){
        chatService.updateMessageStatus(messageId, userId);
    }

    @GetMapping("/chats/{chatId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody

    //needs to be changed in a way that it is dependent on the User that makes the call + Usage of OutgoingMessage(DTO)
    public ArrayList <OutgoingMessageDTO> getMessages(@PathVariable String chatId, @PathVariable Long userId){
        ArrayList<Message> messages = chatService.getAllMessageWithChatId(chatId);
        User user = userService.findByUserId(userId);
        String userLanguage = user.getLanguage();
        ArrayList<OutgoingMessageDTO> outgoingMessageDTOS = new ArrayList<>();

        for(Message message: messages){
            chatService.updateMessageStatus(message.getChatId(), userId);
            OutgoingMessage outgoingMessage = chatService.transformMessageToOutput(message, userLanguage);
            outgoingMessageDTOS.add(DTOMapper.INSTANCE.convertOutgoingMessageToOutgoingMessageDTO(outgoingMessage));
        }
        /*for(ChatMessageDTO chatMessageDTO: chatMessageDTOs){
            for(Long userId: chatRepository.findByChatId(chatId).getUserIds()){
                User user = userService.findByUserId(userId);
                chatMessageDTO.setUserLanguageMapping(userId,user.getLanguage());
            }
            
   
        }

         */
        return outgoingMessageDTOS;
    }
    /*
    @PostMapping("/chat/{chatId}/message")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void sendMessageRequest(@PathVariable String chatId, @RequestHeader("userId") Long userId, @RequestBody MessageContentDTO messageContentDTO){
        String content = messageContentDTO.getContent();
        Message message = chatService.CreateMessage(content,userId, chatId);
        chatService.saveMessage(message);
    }
    */


    @PostMapping("/chats")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createChat(@RequestBody ArrayList<Long> userIds) {
        if (userIds == null || userIds.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A chat must have at least two users.");
        }

        ArrayList<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userService.findByUserId(userId);
            users.add(user);
        }
    Chat newChat = chatService.createChat(users);
    return newChat.getChatId();
    }
}


