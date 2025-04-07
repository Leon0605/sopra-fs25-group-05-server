package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CHAT")
public class Chat implements Serializable {
    @Id
    private String chatId;
    private long userId;
    private ArrayList<String> messagesId = new ArrayList<>();
    private HashSet<String> languages = new HashSet<>();

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
    
    public void setMessagesId(String messageId){
        this.messagesId.add(messageId);
    }   

    public ArrayList <String> getMessagesId(){
        return messagesId;
    }

    public void setLanguages(HashSet<String> languages){this.languages=languages;}
    public HashSet<String> getLanguages(){return languages;}

    private ArrayList<Long> userIds;

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }

    public ArrayList<Long> getUserIds() {
        return userIds;
    }


}
