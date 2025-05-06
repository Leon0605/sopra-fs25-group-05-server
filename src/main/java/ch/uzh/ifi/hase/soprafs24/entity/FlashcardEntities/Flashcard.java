package ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FLASHCARD")
public class Flashcard {
    @Id
    private String flashcardId;

    private Long userId;
    private String learningLanguage;
    private String contentFront;
    private String contentBack;
    private String flashcardSetId;
    private String language;
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

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId =userId;
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

    public String getFlashcardSetId(){
        return flashcardSetId;
    }

    public void setFlashcardSetId(String flashcardSetId){
        this.flashcardSetId =flashcardSetId;
    }
}
