package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE")
public class Message implements Serializable  {

    @Id
    private String messageId;

    private String chatId;
    private Long userId;
    private ArrayList <String> content = new ArrayList<>();

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
    public void setContent(String content){
        this.content.add(content);
    }
    public ArrayList<String> getContent(){
        return content;
    }
}
