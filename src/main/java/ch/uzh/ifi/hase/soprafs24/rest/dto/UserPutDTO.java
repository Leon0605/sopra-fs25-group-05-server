package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserPutDTO {
    private String language;
    private String learningLanguage;
    private String privacy;
    private String birthday;

    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return language;
    }
    public void setLearningLanguage(String learningLanguage){
        this.learningLanguage = learningLanguage;
    }
    public String getLearningLanguage(){
        return learningLanguage;
    }
    public void setPrivacy(String privacy){
        this.privacy = privacy;
    }
    public String getPrivacy(){
        return privacy;
    }
    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
    public String getBirthday(){
        return birthday;
    }
}
