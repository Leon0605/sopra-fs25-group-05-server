package ch.uzh.ifi.hase.soprafs24.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Acl;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserChangePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;



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
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, @Lazy ChatService chatService, ChatRepository chatRepository, MessageRepository messageRepository) {
    this.userRepository = userRepository;
    this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
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

      if (photo.isEmpty() || !photo.getContentType().equals("image/png")) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Picture format is wrong");
      }

      try{
          String bucketName = "sopra-group5-profile-pictures";
          String objectName =  userId.toString() + ".png";

          Storage storage = StorageOptions.getDefaultInstance().getService();

          BlobId blobId = BlobId.of(bucketName, objectName);
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                  .setContentType(photo.getContentType())
                  .build();

          storage.create(blobInfo, photo.getBytes());


          String publicUrl = "https://storage.googleapis.com/" + bucketName + "/" + objectName;
          User user = findByUserId(userId);
          user.setPhoto(publicUrl);
          userRepository.save(user);
          userRepository.flush();
          // Optionally save URL to user's DB record
          // userService.updateProfilePictureUrl(id, publicUrl);

          //return ResponseEntity.ok(Map.of("url", publicUrl));

      } catch (IOException e) {
          throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Upload failed");
      }
    }

    public void deleteProfilePicture(Long userId){
        User user = findByUserId(userId);
        user.setPhoto(null);
    }

  @Transactional
  public void updateUserWithUserId(Long userId, UserPutDTO userPutDTO){
    User user = findByUserId(userId);
    if(userPutDTO.getLanguage() != null){
      String language = userPutDTO.getLanguage();
      user.setLanguage(language);
      ArrayList <Message> updatedMessages = new ArrayList<>();
      ArrayList <Chat> updatedChats = new ArrayList<>();
      for(String chatId: user.getChats()){
          Chat chat = chatRepository.findByChatId(chatId);
          for(String messageId: chat.getMessagesId()){
              Message message = messageRepository.findByMessageId(messageId);
              if(message.getLanguageMapping().getContent(language) == null){
                  String translation = AzureAPI.AzureTranslate(message.getOriginal(), message.getOriginalLanguage(), language);
                  message.getLanguageMapping().setContent(language, translation);
                  updatedMessages.add(message);
              }
          }
          HashSet<String> chatLanguages = new HashSet<>();
          for(Long userIds: chat.getUserIds()){
              chatLanguages.add(findByUserId(userIds).getLanguage());
          }
          chat.setLanguages(chatLanguages);
          updatedChats.add(chat);
      }
      chatRepository.saveAll(updatedChats);
      chatRepository.flush();
      messageRepository.saveAll(updatedMessages);
      messageRepository.flush();
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

  public User getSingleUser(long userId, String token){
        System.out.println(token);
        User user = findByUserId(userId);
        User privacyUser = new User();
      privacyUser.setPrivacy(user.getPrivacy());
      privacyUser.setUsername(user.getUsername());
      privacyUser.setId(user.getId());
      privacyUser.setPhoto(user.getPhoto());
        if (user.getToken().equals(token)) {
            return user;
        }
        else if(user.getPrivacy().equals("open") || user.getFriendsList().contains(findByUserToken(token).getId())){
            privacyUser.setStatus(user.getStatus());
            privacyUser.setBirthday(user.getBirthday());
            privacyUser.setLearningLanguage(user.getLearningLanguage());
            privacyUser.setLanguage(user.getLanguage());
            return privacyUser;
        }
        else if(user.getPrivacy().equals("private")){
            return privacyUser;
        }
        else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something unexpected happened");
        }

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
    newUser.setLearningLanguage("en");
    newUser.setPrivacy("private");
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
    if(sender.getReceivedFriendRequestsList().contains(receiverUserId)){
        handleFriendRequest(sender.getId(), senderUserToken, receiverUserId, true);
        return;
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

  public void handleFriendRequest(Long receiverUserId, String receiverUserToken, Long senderUserId, boolean accept){
    User receiver = findByUserId(receiverUserId);
    User sender = findByUserId(senderUserId);

    if(!receiver.getToken().equals(receiverUserToken)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The provided Token for the friend request receiver is not vaild!");
    }
    if(receiver.getFriendsList().contains(sender.getId()) && sender.getFriendsList().contains(receiver.getId())){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Users are already friends");
    }
    sender.getSentFriendRequestsList().remove(receiverUserId);
    receiver.getReceivedFriendRequestsList().remove(senderUserId);

    if(accept) {
        sender.setFriend(receiverUserId);
        receiver.setFriend(senderUserId);

        ArrayList<User> users = new ArrayList<>();
        users.add(sender);
        users.add(receiver);
        chatService.createChat(users);
    }
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
