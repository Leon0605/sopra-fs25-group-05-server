package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.ArrayList;

public class ChatMessageDTO {
    private String messageId;
    private String chatId;
    private Long userId;
    private ArrayList <String> content;

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
    public void setContent(ArrayList<String> content){
        this.content = content;
    }
    public ArrayList<String> getContent(){
        return content;
    }
}
