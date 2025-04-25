package ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;

@Entity
@Table(name = "MESSAGE")
public class Message implements Serializable  {

    @Id
    private String messageId;

    private String chatId;
    private Long userId;

    @Embedded
    private LanguageMapping languageMapping;

    private String original;

    private LocalDateTime timestamp;

    private String originalLanguage;
    
    
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp =timestamp;
    }

    public LanguageMapping getLanguageMapping() {
        return languageMapping;
    }
    public void setLanguageMapping(LanguageMapping languageMapping) {
        this.languageMapping = languageMapping;
    }

    public void setMessageId(String messageId){
        this.messageId=messageId;
    }
    public String getMessageId(){
        return messageId;
    }
    public void setChatId(String chatId){
        this.chatId=chatId;
    }
    public String getChatId(){
        return chatId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
    public Long getUserId(){
        return userId;
    }

    public String getOriginal(){return original;}
    public void setOriginal(String original){this.original=original;}

    public String getOriginalLanguage(){return originalLanguage;}
    public void setOriginalLanguage(String originalLanguage){this.originalLanguage=originalLanguage;}
    
}
