package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
//Initial commit: Server Test
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
  public void logout(@RequestBody UserPostDTO userPostDTO){

    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    userService.performLogout(userInput);    
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
