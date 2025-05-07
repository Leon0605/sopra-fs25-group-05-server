package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories.ChatRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;


/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;


  @Qualifier("chatRepository")
  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
    // given
    

    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    

    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("TEST");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("TEST");
    // change the name but forget about the username


    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
  @Test
  public void sentFriendRequestValidInput(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    userService.createFriendRequest(createdUser2.getId(),createdUser.getToken());
    
    User updatedUser = userService.findByUserId(createdUser.getId());
    User updatedUser2 = userService.findByUserId(createdUser2.getId());

    assertEquals(updatedUser2.getReceivedFriendRequestsList().get(0), updatedUser.getId());
    assertEquals(updatedUser.getSentFriendRequestsList().get(0), updatedUser2.getId());
  }
  @Test
  public void sentFriendRequestInvalidInputAlreadySent(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    createdUser.setSentFriendRequest(createdUser2.getId());
    createdUser2.setReceivedFriendRequest(createdUser.getId());

    userRepository.save(createdUser);
    userRepository.save(createdUser2);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createFriendRequest(createdUser2.getId(),createdUser.getToken()));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }
  @Test
  public void sentFriendRequestInvalidInputAlreadyFriends(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    createdUser.setFriend(createdUser2.getId());
    createdUser2.setFriend(createdUser.getId());

    userRepository.save(createdUser);
    userRepository.save(createdUser2);
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createFriendRequest(createdUser2.getId(),createdUser.getToken()));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());

  }
  @Test
  public void acceptFriendRequestValidInput(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    userService.createFriendRequest(createdUser2.getId(),createdUser.getToken());
    
    User updatedUser = userService.findByUserId(createdUser.getId());
    User updatedUser2 = userService.findByUserId(createdUser2.getId());

    assertTrue(chatRepository.findAll().isEmpty());
    userService.handleFriendRequest(updatedUser2.getId(), updatedUser2.getToken(), updatedUser.getId(), true);

    updatedUser = userService.findByUserId(createdUser.getId());
    updatedUser2 = userService.findByUserId(createdUser2.getId());

    assertEquals(updatedUser2.getFriendsList().get(0), updatedUser.getId());
    assertEquals(updatedUser.getFriendsList().get(0), updatedUser2.getId());
    assertFalse(chatRepository.findAll().isEmpty());
    
  }
  @Test
  public void acceptFriendRequestInvalidInputNotMatchingToken(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    userService.createFriendRequest(createdUser2.getId(),createdUser.getToken());
    
    User updatedUser = userService.findByUserId(createdUser.getId());
    User updatedUser2 = userService.findByUserId(createdUser2.getId());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.handleFriendRequest(updatedUser2.getId(), "WrongToken", updatedUser.getId(), true));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
  }
  @Test
  public void acceptFriendRequestInvalidInputAlreadyFriends(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    User testUser2 = new User();
    testUser2.setPassword("Password2");
    testUser2.setUsername("Receiver");
    User createdUser2 = userService.createUser(testUser2);

    createdUser.setFriend(createdUser2.getId());
    createdUser2.setFriend(createdUser.getId());

    userRepository.save(createdUser);
    userRepository.save(createdUser2);

    User updatedUser = userService.findByUserId(createdUser.getId());
    User updatedUser2 = userService.findByUserId(createdUser2.getId());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.handleFriendRequest(updatedUser2.getId(), updatedUser2.getToken(), updatedUser.getId(), true));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void updateUserPhotoValidInput(){
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    byte[] imageBytes = "UserPhoto".getBytes();
    MockMultipartFile photo = new MockMultipartFile("userPhoto", imageBytes);

    assertNull(createdUser.getPhoto());
    userService.updateUserProfilePictureWithUserId(createdUser.getId(), photo);

    User updatedUser = userService.findByUserId(createdUser.getId());

    assertNotNull(updatedUser.getPhoto());
    assertTrue(updatedUser.getPhoto().startsWith("data:image/png;base64,"));
  }
  
  @Test
  public void updateUserPhotoInvalidInput() throws Exception{
    User testUser = new User();
    testUser.setPassword("Password");
    testUser.setUsername("Sender");
    User createdUser = userService.createUser(testUser);

    
    MultipartFile badPhoto = Mockito.mock(MultipartFile.class);
    Mockito.when(badPhoto.getBytes()).thenThrow(new IOException("invalid format"));
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUserProfilePictureWithUserId(createdUser.getId(), badPhoto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
  }



}
