package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;


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
    
}
