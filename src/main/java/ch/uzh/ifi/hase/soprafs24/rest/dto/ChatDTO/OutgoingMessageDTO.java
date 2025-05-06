package ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO;

public class OutgoingMessageDTO {
    private String messageId;
    private String chatId;
    private Long userId;
    private String timestamp;
    private String originalMessage;
    private String translatedMessage;
    private String status;

    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public void setTimestamp(String timestamp){this.timestamp = timestamp;}
    public void setOriginalMessage(String originalMessage) {this.originalMessage = originalMessage;}
    public void setTranslatedMessage(String translatedMessage) {this.translatedMessage = translatedMessage;}
    public void setStatus(String status) {this.status = status;}

    public String getTimestamp(){
        return timestamp;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getChatId() {
        return chatId;
    }
    public Long getUserId() {
        return userId;
    }
    public String getOriginalMessage() {
        return originalMessage;
    }
    public String getTranslatedMessage() {
        return translatedMessage;
    }
    public String getStatus() {return status;}
}
