package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.ArrayList;

public class UserChatDTO {
    private String chatId; 
    private ArrayList<Long> userIds;

    public void setChatId(String chatId){
        this.chatId=chatId;
    }
    public String getChatId(){
        return chatId;
    }

    public void setUserIds(ArrayList<Long> userIds){
        this.userIds = userIds;
    }
    public ArrayList<Long> getUserIds(){
        return userIds;
    }
    


    

}
