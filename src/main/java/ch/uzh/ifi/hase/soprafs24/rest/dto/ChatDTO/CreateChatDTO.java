package ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO;

import java.util.ArrayList;

public class CreateChatDTO {
    private String chatName;
    private ArrayList<Long> userIds;

    public void setChatName(String chatName){this.chatName=chatName;}
    public String getChatName(){return chatName;}

    public void setUserIds(ArrayList<Long> userIds){this.userIds=userIds;}
    public ArrayList<Long> getUserIds(){return userIds;}
}
