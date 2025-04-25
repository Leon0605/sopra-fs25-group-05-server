package ch.uzh.ifi.hase.soprafs24.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserChangePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;



/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final ChatService chatService;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository,@Lazy ChatService chatService) {
    this.userRepository = userRepository;
    this.chatService = chatService;
  }

  public void changeUserPassword(Long userId, String token, UserChangePasswordDTO userChangePasswordDTO){
    User user = findByUserId(userId);
    if(!user.getToken().equals(token) ){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token not valid for this user");
    }
    if(!user.getPassword().equals(userChangePasswordDTO.getOldPassword())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"wrong old password ");
    }
    user.setPassword(userChangePasswordDTO.getNewPassword());
    userRepository.save(user);
    userRepository.flush();    
  }

  public void updateUserProfilePictureWithUserId(Long userId, MultipartFile photo){

    User user = findByUserId(userId);
    String dataUrl;
    try {
      String base64 = Base64.getEncoder().encodeToString(photo.getBytes());
      dataUrl = "data:image/png;base64," + base64;
     
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Picture format is wrong");
    }
    user.setPhoto(dataUrl);
    userRepository.save(user);
    userRepository.flush();
  }

  public void updateUserWithUserId(Long userId, UserPutDTO userPutDTO){
    User user = findByUserId(userId);
    if(userPutDTO.getLanguage() != null){
      user.setLanguage(userPutDTO.getLanguage());
    }
    if(userPutDTO.getLearningLanguage() != null){
      user.setLearningLanguage(userPutDTO.getLearningLanguage());
    }
    if(userPutDTO.getPrivacy() != null){
      user.setPrivacy(userPutDTO.getPrivacy());
    }
    if(userPutDTO.getBirthday() != null){
      user.setBirthday(userPutDTO.getBirthday());
    }
  
    

    userRepository.save(user);
    userRepository.flush();
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }
  public void performLogout(Long userId){

    User foundUser = findByUserId(userId);
    if( foundUser == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
    }
    //User convertedUser = foundUser;
    foundUser.setStatus(UserStatus.OFFLINE);
    userRepository.save(foundUser);
    userRepository.flush();
  }

  public User verifyLogin(User user){

    User foundUser = userRepository.findByUsername(user.getUsername());
    if( foundUser == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Username not found");
    }
    if(!foundUser.getPassword().equals(user.getPassword())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Wrong Password");
    }
    foundUser.setStatus(UserStatus.ONLINE);
    return foundUser;
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setLanguage("en");
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    
    newUser = userRepository.save(newUser);
    userRepository.flush();
       
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public void createFriendRequest(Long receiverUserId, String senderUserToken){
    User sender = findByUserToken(senderUserToken);
    User receiver = findByUserId(receiverUserId);
    
    if(receiver.getReceivedFriendRequestsList().contains(sender.getId()) && sender.getSentFriendRequestsList().contains(receiverUserId)){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already sent a friend request to this person!");
    }
    if(receiver.getFriendsList().contains(sender.getId()) && sender.getFriendsList().contains(receiver.getId())){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Users are already friends");
    }

    sender.setSentFriendRequest(receiverUserId);
    receiver.setReceivedFriendRequest(sender.getId());

    userRepository.save(sender);
    userRepository.save(receiver);
    userRepository.flush();
  }

  public void acceptFriendRequest(Long receiverUserId, String reiceiverUserToken, Long senderUserId){
    User receiver = findByUserId(receiverUserId);
    User sender = findByUserId(senderUserId);

    if(!receiver.getToken().equals(reiceiverUserToken)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The provied Token for the friend request receiver is not vaild!");
    }
    if(receiver.getFriendsList().contains(sender.getId()) && sender.getFriendsList().contains(receiver.getId())){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Users are already friends");
    }
    sender.getSentFriendRequestsList().remove(receiverUserId);
    sender.getReceivedFriendRequestsList().remove(receiverUserId);
    receiver.getReceivedFriendRequestsList().remove(senderUserId);
    receiver.getSentFriendRequestsList().remove(senderUserId);

    sender.setFriend(receiverUserId);
    receiver.setFriend(senderUserId);

    ArrayList<User> users = new ArrayList<>();
    users.add(sender);
    users.add(receiver);
    chatService.createChat(users);
    userRepository.save(sender);
    userRepository.save(receiver);
    userRepository.flush();
    
  }
  public User findByUserToken(String token){

    User user = userRepository.findByToken(token);
    if(user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with Token not found");
    }
    return user;
  }

  public User findByUserId(Long userId){
    Optional<User> userOptional = userRepository.findById(userId);
    if(userOptional.isEmpty()){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with Id not found");
    }
    return userOptional.get();
  }

  public ArrayList <String> findChatWithUserId(Long id){
    
    User user = findByUserId(id);
    return user.getChats() != null ? user.getChats() : new ArrayList<>();
  }
  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    
    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
  
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username ", "is"));
    } 
  }
}
