package ch.uzh.ifi.hase.soprafs24.service;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;

@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository,MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }
    public Chat createChat(User user1,User user2){
        Chat newChat = new Chat();

        newChat.setUserId(user1.getId());
        newChat.setUserId(user2.getId());
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
    public Message CreateMessage(String content,Long userId,String ChatId){
        Message message = new Message();
        message.setChatId(ChatId);
        message.setUserId(userId);
        message.setMessageId(UUID.randomUUID().toString());
        message.setContent(content);
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
