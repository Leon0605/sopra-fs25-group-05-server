package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserGetDTO {

  private Long id;

  private String username;
  private UserStatus status;
  private String language;
  private String learningLanguage;
  private String birthday;
  private String privacy;
  private String photo;

  public String getPhoto() {
      return photo;
  }
  public void setPhoto(String photo) {
      this.photo = photo;
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
  public void setLanguage(String language){
    this.language = language;
  }

  public String getLanguage(){
      return language;
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
