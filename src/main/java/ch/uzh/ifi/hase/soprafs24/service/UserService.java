package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
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
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

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
  public void updateLanguageWithUserId(Long userId,String language){
    User user = findByUserId(userId);
    user.setLanguage(language);

    userRepository.save(user);
    userRepository.flush();
  }
  public List<User> getUsers() {
    return this.userRepository.findAll();
  }
  public void performLogout(Long userId){

    Optional<User> foundUser = userRepository.findById(userId);
    if( foundUser.isEmpty()){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
    }
    User convertedUser = foundUser.get();
    convertedUser.setStatus(UserStatus.OFFLINE);
    userRepository.save(convertedUser);
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
    List <User> otherUsers = !getUsers().isEmpty() ? getUsers() : new ArrayList<User>();  
    newUser = userRepository.save(newUser);
    userRepository.flush();
    for(User user: otherUsers){
      if(!user.getId().equals(newUser.getId()) ){
        ArrayList<User> users = new ArrayList<>();
        users.add(newUser);
        users.add(user);
        Chat newChat = chatService.createChat(users); 
        user.setChats(newChat.getChatId());
        newUser.setChats(newChat.getChatId());
        userRepository.save(user);
        userRepository.save(newUser);
        userRepository.flush();
      }
      
    }
    
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User findByUserId(Long userId){
    Optional<User> userOptional = userRepository.findById(userId);
    if(userOptional.isEmpty()){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
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
