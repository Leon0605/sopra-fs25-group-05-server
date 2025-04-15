package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import java.io.Serializable;

public class OutgoingMessage {
    private String messageId;
    private String chatId;
    private Long userId;
    //private String timestamp;
    private String originalMessage;
    private String translatedMessage;

    //private String timestamp;

    public void setMessageId(String messageId){this.messageId = messageId;};
    public void setChatId(String chatId){this.chatId = chatId;};
    public void setUserId(Long userId){this.userId = userId;};
    public void setOriginalMessage(String originalMessage){this.originalMessage = originalMessage;};
    public void setTranslatedMessage(String translatedMessage){this.translatedMessage = translatedMessage;};

    public String getMessageId() {return messageId;}
    public String getChatId() {return chatId;}
    public Long getUserId() {return userId;}
    public String getOriginalMessage() {return originalMessage;}
    public String getTranslatedMessage() {return translatedMessage;}
}
