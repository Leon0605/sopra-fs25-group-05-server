package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.HashMap;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.constant.LanguageMapping;

public class ChatMessageDTO {
    private String messageId;
    private String chatId;
    private Long userId;
    private LanguageMapping languageMapping;
    //private Map <Long,String> userLanguageMapping = new HashMap();
    
    public void setMessageId(String messageId){
        this.messageId = messageId;
    }
    public String getMessageId(){
        return messageId;
    }
    public void setChatId(String chatId){
        this.chatId = chatId;
    }
    public String getChatId(){
        return chatId;
    }
    public void setUserId(Long userId){
        this.userId = userId;
    }
    public Long getUserId(){
        return userId;
    }
    //public void setLanguageMapping(LanguageMapping languageMapping){
        //this.languageMapping = languageMapping;
    //}
    public LanguageMapping getLanguageMapping(){
        return languageMapping;
    }
    /*
    public void setUserLanguageMapping(long userId,String language){
        userLanguageMapping.put(userId,language);
    }
    public Map getUserLanguageMapping(){
        return userLanguageMapping;
    }

     */
}
