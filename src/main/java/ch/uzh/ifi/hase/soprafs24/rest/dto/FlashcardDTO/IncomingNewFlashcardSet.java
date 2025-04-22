package ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

public class IncomingNewFlashcardSet {
    private String flashcardSetName;

    public void setFlashcardSetName(String flashcardSetName){
        this.flashcardSetName = flashcardSetName;
    }
    public String getFlashcardSetName(){
        return flashcardSetName;
    }
}
