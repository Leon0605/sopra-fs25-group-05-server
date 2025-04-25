package ch.uzh.ifi.hase.soprafs24.service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;


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
        }
        userRepository.flush();
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
        message.setOriginal(originalMessage);
        message.setChatId(incomingMessage.getChatId());
        message.setUserId(senderID);

        String senderLanguage = userService.findByUserId(senderID).getLanguage();
        languageMap.setContent(senderLanguage, originalMessage);


        message.setMessageId(generateId());
        message.setLanguageMapping(languageMap);
        message.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Zurich")));
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
        outgoingMessage.setTimestamp(message.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy,HH:mm")));
        outgoingMessage.setMessageId(message.getMessageId());
        outgoingMessage.setChatId(message.getChatId());
        outgoingMessage.setUserId(SenderId);
        outgoingMessage.setOriginalMessage(message.getOriginal());
        outgoingMessage.setTranslatedMessage(message.getLanguageMapping().getContent(Language));
        return outgoingMessage;
    }

    public String generateId(){  //can be private but in order to test is set to public
        return UUID.randomUUID().toString();
    }
}
