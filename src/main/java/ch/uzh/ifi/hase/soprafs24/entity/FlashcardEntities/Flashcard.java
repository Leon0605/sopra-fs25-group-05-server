package ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardStatus;

@Entity
@Table(name = "FLASHCARD")
public class Flashcard {
    @Id
    private String flashcardId;

    private Long userId;
    private String learningLanguage;

    @Lob
    @Column(name="content_front", columnDefinition="BLOB")
    private String contentFront;
    @Lob
    @Column(name="content_back", columnDefinition="BLOB")
    private String contentBack;
    
    private String flashcardSetId;
    private String language;
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
