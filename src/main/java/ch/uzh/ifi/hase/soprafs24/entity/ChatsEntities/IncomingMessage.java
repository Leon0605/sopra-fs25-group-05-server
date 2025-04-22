package ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "INCOMINGMESSAGE")
public class IncomingMessage implements Serializable {
    private String chatId;

    @Id
    private Long userId;


    private String content;


    public String getChatId() {return chatId;}
    public void setChatId(String chatId) {this.chatId = chatId;}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getContent() {return content;}
    public void setContent(String originalMessage) {this.content = originalMessage;}
}
