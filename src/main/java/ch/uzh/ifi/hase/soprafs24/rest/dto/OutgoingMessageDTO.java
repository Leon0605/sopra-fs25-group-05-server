package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class OutgoingMessageDTO {
    private String messageId;
    private String chatId;
    private Long userId;
    //private String timestamp
    private String originalMessage;
    private String translatedMessage;

    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public void setOriginalMessage(String originalMessage) {this.originalMessage = originalMessage;}
    public void setTranslatedMessage(String translatedMessage) {this.translatedMessage = translatedMessage;}
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
}
