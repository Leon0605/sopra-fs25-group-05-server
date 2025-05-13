package ch.uzh.ifi.hase.soprafs24.entity.UserEntities;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
//:)
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private String password;

  @Lob
  @Column(name="chats_ids", columnDefinition="BLOB")
  private ArrayList<String> chats = new ArrayList<>();
  
  private String language;

  private String learningLanguage;

  private String privacy;

  private String birthday;

  private ArrayList<Long> friendsList = new ArrayList<>();

  private ArrayList<Long> receivedFriendRequestsList = new ArrayList<>();

  private ArrayList<Long> sentFriendRequestsList = new ArrayList<>();
  
  @Lob
  @Column(name="flashcards_sets_ids", columnDefinition="BLOB")
  private ArrayList<String> flashcardSetsIds = new ArrayList<>();

  @Lob
  private byte[] photo;

  public void setFlashcardSetId(String flashcardSetId){
    this.flashcardSetsIds.add(flashcardSetId);
  }
  public ArrayList<String> getFlashcardSetsIds(){
    return flashcardSetsIds;
  }

  public void setReceivedFriendRequestsList(ArrayList<Long> receivedList){
    this.receivedFriendRequestsList = receivedList;
  }
  public void setReceivedFriendRequest(Long userId){
    this.receivedFriendRequestsList.add(userId);
  }
  public ArrayList<Long> getReceivedFriendRequestsList(){
    return receivedFriendRequestsList;
  }

  public void setSentFriendRequestsList(ArrayList<Long> sentList){
    this.sentFriendRequestsList = sentList;
  }
  public void setSentFriendRequest(Long userId){
    this.sentFriendRequestsList.add(userId);
  }
  public ArrayList<Long> getSentFriendRequestsList(){
    return sentFriendRequestsList;
  }

  public void setFriendsList(ArrayList<Long> friendsList){
    this.friendsList = friendsList;
  }
  public void setFriend(Long userId){
    this.friendsList.add(userId);
  }
  public ArrayList<Long> getFriendsList(){
    return friendsList;
  }

  public void setBirthday(String birthday){
    this.birthday = birthday;
  }

  public String getBirthday(){
    return birthday;
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
  
  public byte[] getPhoto() {
      return photo;
  }
  public void setPhoto(byte[] photo) {
      this.photo = photo;
  }
  public void setChats(String chatId){
    this.chats.add(chatId);
  }
  public ArrayList <String> getChats(){
    return chats;
  }
  public void setPassword(String password){
    this.password=password;
  }
  public String getPassword(){
    return password;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
  public String getLanguage(){
    return language;
  }
  public void setLanguage(String language){
    this.language=language;
  }
}
