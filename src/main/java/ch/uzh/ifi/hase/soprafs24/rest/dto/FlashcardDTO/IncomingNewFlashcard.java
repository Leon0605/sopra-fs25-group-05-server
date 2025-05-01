package ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

public class IncomingNewFlashcard {
    private String contentFront;
    private String contentBack;

    public void setContentFront(String contentFront){
        this.contentFront = contentFront;
    }
    public void setContentBack(String contentBack){
        this.contentBack = contentBack;
    }

    public String getContentFront(){
        return contentFront;
    }

    public String getContentBack(){
        return contentBack;
    }


}
