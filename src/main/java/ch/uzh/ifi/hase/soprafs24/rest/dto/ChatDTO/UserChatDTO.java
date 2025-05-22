package ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO;

import java.util.ArrayList;

public class UserChatDTO {
    private String chatId; 
    private ArrayList<Long> userIds;
    private String name;

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

    public void setName(String name){this.name=name;}
    public String getName(){return name;}


}
