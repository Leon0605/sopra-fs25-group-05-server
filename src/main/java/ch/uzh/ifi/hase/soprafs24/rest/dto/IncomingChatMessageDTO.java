package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class IncomingChatMessageDTO {
    private String chatId;
    private Long userId;
    private String content;

    public String getChatId() {return chatId;}
    public void setChatId(String chatId) {this.chatId = chatId;}

    public Long getUserId(){return userId;}
    public void setUserId(Long senderId){this.userId = senderId;}

    public String getContent(){return content;}
    public void setContent(String originalMessage){this.content = originalMessage;}

}
