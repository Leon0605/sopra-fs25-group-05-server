package ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

import java.util.HashMap;
import java.util.Map;

public class FlashcardSetGetDTO {
    private String flashcardSetName;
    private int flashcardQuantity;
    private String learningLanguage;
    private String flashcardSetId;
    private String language;
    private Map<String,Float> statistic = new HashMap<>();
    
    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return language;
    }

    public void setFlashcardSetName(String flashcardSetName){
        this.flashcardSetName = flashcardSetName;
    }
    public String getFlashcardSetName(){
        return flashcardSetName;
    }
    public void setFlashcardQuantity(int flashcardQuantity){
        this.flashcardQuantity = flashcardQuantity;
    }
    public int getFlashcardQuantity(){
        return flashcardQuantity;
    }
    public void setFlashcardSetId(String flashcardSetId){
        this.flashcardSetId =flashcardSetId;
    }
    public void setLearningLanguage(String learningLanguage){
        this.learningLanguage =learningLanguage;
    }
    public String getFlashcardSetId(){
        return flashcardSetId;
    }
    public String getLearningLanguage() {
        return learningLanguage;
    }
    public Map<String, Float> getStatistic() {
        return statistic;
      }
      public void setStatistic(Map<String, Float> statistic) {
        this.statistic = statistic;
      }
}
