package ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardStatus;

public class FlashcardGetDTO {
    private String flashcardId;
    private String learningLanguage;
    private String language;
    private String contentFront;
    private String contentBack;
    private FlashcardStatus status;
    
    public void setStatus(FlashcardStatus status){
        this.status = status;
    }
    public FlashcardStatus getStatus(){
        return status;
    }
    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return language;
    }
    public String getFlashcardId(){
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId){
        this.flashcardId =flashcardId;
    }

    public String getLearningLanguage(){
        return learningLanguage;
    }

    public void setLearningLanguage(String learningLanguage){
        this.learningLanguage = learningLanguage;
    }

    public String getContentFront(){
        return contentFront;
    }

    public void setContentFront(String contentFront){
        this.contentFront =contentFront;
    }

    public String getContentBack(){
        return contentBack;
    }

    public void setContentBack(String contentBack){
        this.contentBack = contentBack;
    }

}
