package ch.uzh.ifi.hase.soprafs24.service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;
import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;


@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository,MessageRepository messageRepository,UserService userService,UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userService =userService;
        this.userRepository = userRepository;
    }
    public Chat createChat(ArrayList<User> users) {
        if (users == null || users.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A chat must have at least two users.");
        }
    
        Chat newChat = new Chat();
        ArrayList<Long> userIds = new ArrayList<>();
        HashSet<String> languages = new HashSet<>();

        newChat.setUserIds(userIds); 
        newChat.setChatId(generateId());
        newChat.setLanguages(languages);

        for (User user : users) {
            userIds.add(user.getId());
            languages.add(user.getLanguage());
            user.setChats(newChat.getChatId());
            userRepository.save(user);
            userRepository.flush();
        }

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
    public Message CreateMessage(IncomingMessage incomingMessage){
        Message message = new Message();

        LanguageMapping languageMap= new LanguageMapping();
        Long senderID = incomingMessage.getUserId();
        String originalMessage = incomingMessage.getContent();
        message.setChatId(incomingMessage.getChatId());
        message.setUserId(senderID);
        //ArrayList<User> messageReceivers = new ArrayList<>();

        //for(Long receiverId: chatRepository.findByChatId(chatId).getUserIds()){
            //if(!receiverId.equals(senderId)){
                //messageReceivers.add(userService.findByUserId(receiverId));
            //}
            
        //}

        String senderLanguage = userService.findByUserId(senderID).getLanguage();
        languageMap.setContent(senderLanguage, originalMessage);

        /*for(User receiverUser : userService.findByUserId(chatRepository.findByChatId(chatId).getUserIds())){
            String receiverLanguage = receiverUser.getLanguage();
            if(!receiverLanguage.equals(senderLanguage)){
                String translation = AzureAPI.AzureTranslate(messageText, senderLanguage, receiverLanguage);
                languageMap.setContent(receiverLanguage,translation);
            }
        }

         */

        message.setMessageId(generateId());
        message.setLanguageMapping(languageMap);
        //timestamp missing
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

    public OutgoingMessage transformMessageToOutput(Message message, String Language){
        long SenderId = message.getUserId();
        User sender = userService.findByUserId(SenderId);
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setMessageId(message.getMessageId());
        outgoingMessage.setChatId(message.getChatId());
        outgoingMessage.setUserId(SenderId);
        outgoingMessage.setOriginalMessage(message.getLanguageMapping().getContent(sender.getLanguage()));
        outgoingMessage.setTranslatedMessage(message.getLanguageMapping().getContent(Language));
        return outgoingMessage;
    }

    public String generateId(){  //can be private but in order to test is set to public
        return UUID.randomUUID().toString();
    }
}
