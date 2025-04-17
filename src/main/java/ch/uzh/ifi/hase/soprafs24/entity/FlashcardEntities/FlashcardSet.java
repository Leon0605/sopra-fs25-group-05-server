package ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FLASHCARDSET")
public class FlashcardSet {
    @Id
    private String flashcardSetId;

    private ArrayList<String> flashcardsIds = new ArrayList<>();
    private String learningLanguage;
    private Long userId;
    private String flashcardSetName;

    public void setFlashcardSetName(String flashcardSetName){
        this.flashcardSetName = flashcardSetName;
    }
    public String getFlashcardSetName(){
        return flashcardSetName;
    }

    public void setFlashcardSetId(String flashcardSetId){
        this.flashcardSetId =flashcardSetId;
    }
    public void setFlashcardsIds(String flashcardId){
        this.flashcardsIds.add(flashcardId);
    }

    public void setLearningLanguage(String learningLanguage){
        this.learningLanguage =learningLanguage;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getFlashcardSetId(){
        return flashcardSetId;
    }

    public ArrayList<String> getFlashcardsIds(){
        return flashcardsIds;
    }


    public String getLearningLanguage() {
        return learningLanguage;
    }

    public Long getUserId() {
        return userId;
    }
    
}
