package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.ArrayList;

public class UserChatDTO {
    private String chatId;
    
    private long userId;

    public void setChatId(String chatId){
        this.chatId=chatId;
    }
    public String getChatId(){
        return chatId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }
    public long getUserId(){
        return userId;
    }
    


    

}
