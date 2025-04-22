package ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

public class FlashcardGetDTO {
    private String flashcardId;
    private String learningLanguage;
    private String contentFront;
    private String contentBack;

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
