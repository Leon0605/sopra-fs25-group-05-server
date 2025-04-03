package ch.uzh.ifi.hase.soprafs24.service;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;


@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository,MessageRepository messageRepository,UserService userService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userService =userService;
    }
    public Chat createChat(ArrayList<User> users) {
        if (users == null || users.size() < 2) {
            throw new IllegalArgumentException("A chat must have at least two users.");
        }
    
        Chat newChat = new Chat();
        ArrayList<Long> userIds = new ArrayList<>();
    
        for (User user : users) {
            userIds.add(user.getId());
        }
    
        newChat.setUserIds(userIds); // Ensure the Chat class has a setUserIds method
        newChat.setChatId(UUID.randomUUID().toString());
    
        chatRepository.save(newChat);
        chatRepository.flush();
    
        return newChat;
    }
    public ArrayList<Message> getAllMessageWithChatId(String chatId){
        Chat chat = chatRepository.findByChatId(chatId);
        if(chat == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Chat not found");
        }
        ArrayList<Message> messages = new ArrayList<>();

        for(String messageId: chat.getMessagesId()){
            messages.add(messageRepository.findByMessageId(messageId));
        }
        return messages;

    }
    public Message CreateMessage(String messageText,Long senderId,String chatId){
        Message message = new Message();

        LanguageMapping languageMap= new LanguageMapping();

        ArrayList<User> messageReceivers = new ArrayList<>();

        for(Long receiverId: chatRepository.findByChatId(chatId).getUserIds()){
            if(!receiverId.equals(senderId)){
                messageReceivers.add(userService.findByUserId(receiverId));
            }
            
        }

        String senderLanguage = userService.findByUserId(senderId).getLanguage();
        languageMap.setContent(senderLanguage, messageText);

        for(User receiverUser : messageReceivers){
            String receiverLanguage = receiverUser.getLanguage();
            if(!receiverLanguage.equals(senderLanguage)){
                String translation = AzureAPI.AzureTranslate(messageText, senderLanguage, receiverLanguage);
                languageMap.setContent(receiverLanguage,translation);
            }
        }

     
        message.setChatId(chatId);
        message.setUserId(senderId);
        message.setMessageId(UUID.randomUUID().toString());
        message.setLanguageMapping(languageMap);
        return message;
    }
    public void saveMessage(Message message){
        Chat chat = chatRepository.findByChatId(message.getChatId());
        if (chat == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Chat not found");
        }
        messageRepository.save(message);
        messageRepository.flush();
        chat.setMessagesId(message.getMessageId());
        chatRepository.save(chat);
        chatRepository.flush();
    }
}
