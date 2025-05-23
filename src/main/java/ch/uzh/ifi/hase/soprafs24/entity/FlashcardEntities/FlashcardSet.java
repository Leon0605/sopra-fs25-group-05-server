package ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "FLASHCARDSET")
public class FlashcardSet {
    @Id
    private String flashcardSetId;

    @Lob
    @Column(name="flashcard__ids", columnDefinition="BLOB")
    private ArrayList<String> flashcardsIds = new ArrayList<>();
    private String learningLanguage;
    private Long userId;
    @Lob
    @Column(name="flashcard_set_name", columnDefinition="BLOB")
    private String flashcardSetName;
    private String language;

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
    public void setLanguage(String language){
        this.language = language;
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
    
    public String getLanguage(){
        return language;
    }
    
}
