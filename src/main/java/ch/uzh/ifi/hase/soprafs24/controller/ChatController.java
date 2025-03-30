package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;

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

@RestController
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    ChatController(ChatService chatService,UserService userService,ChatRepository chatRepository) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList <UserChatDTO> getAllChats(@RequestHeader("userId") Long userId){
       
        ArrayList <String> chatsId = userService.findChatWithUserId(userId);
        
        ArrayList <UserChatDTO> userChatDTOs = new ArrayList<>();
        
        for(String chatId : chatsId){
            userChatDTOs.add(DTOMapper.INSTANCE.convertChatEntityToUserChatDTO(chatRepository.findByChatId(chatId)));
        }
        return userChatDTOs;
    }
    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList <ChatMessageDTO> getMessages(@PathVariable String chatId){
        ArrayList<Message> messages = chatService.getAllMessageWithChatId(chatId);
        ArrayList<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();

        for(Message message: messages){
            chatMessageDTOs.add(DTOMapper.INSTANCE.convertMessageEntityToChatMessageDTO(message));
        }
        return chatMessageDTOs;
    }
    @PostMapping("/chat/{chatId}/message")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void sendMessageRequest(@PathVariable String chatId, @RequestHeader("userId") Long userId, @RequestBody MessageContentDTO messageContentDTO){
        String content = messageContentDTO.getContent();
        Message message = chatService.CreateMessage(content,userId, chatId);
        chatService.saveMessage(message);
    }

    @PostMapping("/chat")
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