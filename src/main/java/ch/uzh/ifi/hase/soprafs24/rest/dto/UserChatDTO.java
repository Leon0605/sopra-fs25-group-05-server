package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.ArrayList;

public class UserChatDTO {
    private String chatId;
    
    private ArrayList <Long> userId;

    public void setChatId(String chatId){
        this.chatId=chatId;
    }
    public String getChatId(){
        return chatId;
    }

    public void setUserId(ArrayList <Long> userId){
        this.userId = userId;
    }
    public ArrayList <Long> getUserId(){
        return userId;
    }
    


    

}
