package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserChangePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;
  

  UserController(UserService userService) {
    this.userService = userService;
   
  }
  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@PathVariable String userId, @RequestBody UserPutDTO userLanguageUpdateDTO){

    userService.updateUserWithUserId(Long.parseLong(userId),userLanguageUpdateDTO);
  }
  @PostMapping("/users/{userId}/photo")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void updateProfilePicture(@PathVariable String userId, @RequestParam("photo") MultipartFile photo){
   
    userService.updateUserProfilePictureWithUserId(Long.parseLong(userId),photo);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserLoginDTO login(@RequestBody UserPostDTO userPostDTO){

    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User foundUser = userService.verifyLogin(userInput);
    UserLoginDTO userLoginDTO = DTOMapper.INSTANCE.convertEntityToUserLoginDTO(foundUser);

    return userLoginDTO;
  }
  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void logout(@RequestHeader("userId") Long userId){
    
    userService.performLogout(userId);    
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }
  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getOneUser(@PathVariable String userId){
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.findByUserId(Long.parseLong(userId)));  
  }

  @PostMapping("/users/{userId}/change-password")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void changePassword(@PathVariable String userId, @RequestHeader("Authorization") String token, @RequestBody UserChangePasswordDTO userChangePasswordDTO){
      userService.changeUserPassword(Long.parseLong(userId),token,userChangePasswordDTO);
  }

  @PostMapping("/users/{receiverUserId}/friend-request")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void handleIncomingFriendRequest(@PathVariable String receiverUserId, @RequestHeader("Authorization") String senderToken){
      userService.createFriendRequest(Long.parseLong(receiverUserId),senderToken);
  }

  @GetMapping("/users/{userId}/friend-request")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ArrayList<UserGetDTO> getAllReceivedFriendRequest(@PathVariable String userId){

    User user = userService.findByUserId(Long.parseLong(userId));
    
    ArrayList<UserGetDTO> result = new ArrayList<>();

    for(Long senderId : user.getReceivedFriendRequestsList()){
      result.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.findByUserId(senderId)));
    }
    return result;
  }
  @GetMapping("/users/{userId}/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ArrayList<UserGetDTO> getAllFriends(@PathVariable String userId){

    User user = userService.findByUserId(Long.parseLong(userId));
    
    ArrayList<UserGetDTO> result = new ArrayList<>();

    for(Long senderId : user.getFriendsList()){
      result.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.findByUserId(senderId)));
    }
    return result;
  }
  @PutMapping("/users/{receiverUserId}/friend-request")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void handleAcceptFriendRequest(@PathVariable String receiverUserId, @RequestHeader("senderUserId") Long senderUserId, @RequestHeader("Authorization") String receiverUserToken){
    userService.acceptFriendRequest(Long.parseLong(receiverUserId),receiverUserToken, senderUserId);
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(createdUser);
  }
}
