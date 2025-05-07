package ch.uzh.ifi.hase.soprafs24.service;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserChangePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  @Spy
  private UserService userService;

  @Mock
  private ChatService chatService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("Password");
    testUser.setUsername("testUsername");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }


  @Test
  public void changePassword_validInputs_success(){
      testUser.setToken("validToken");
      UserChangePasswordDTO changePasswordDTO = new UserChangePasswordDTO();
      changePasswordDTO.setOldPassword(testUser.getPassword());
      changePasswordDTO.setNewPassword("NewPassword");

      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
      userService.changeUserPassword(testUser.getId(), testUser.getToken(), changePasswordDTO);
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

      assertEquals(testUser.getPassword(), changePasswordDTO.getNewPassword());
  }

  @Test
  public void changePassword_invalidUserId_throwsException(){
      testUser.setToken("Token");
      UserChangePasswordDTO changePasswordDTO = new UserChangePasswordDTO();
      changePasswordDTO.setOldPassword(testUser.getPassword());
      changePasswordDTO.setNewPassword("NewPassword");

      Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).findByUserId(Mockito.anyLong());

      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.changeUserPassword(testUser.getId(), testUser.getToken(), changePasswordDTO));
      assertEquals(404, response.getStatus().value());
  }

  @Test
  public void changePassword_invalidToken_throwsException(){
      testUser.setToken("RealToken");
      String invalidToken = "invalidToken";
      UserChangePasswordDTO changePasswordDTO = new UserChangePasswordDTO();
      changePasswordDTO.setOldPassword(testUser.getPassword());
      changePasswordDTO.setNewPassword("NewPassword");

      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.changeUserPassword(testUser.getId(), invalidToken, changePasswordDTO));
      assertEquals(401, response.getStatus().value());
  }

  @Test
  public void changePassword_invalidOldPassword_throwsException(){
      testUser.setToken("validToken");
      UserChangePasswordDTO changePasswordDTO = new UserChangePasswordDTO();
      changePasswordDTO.setOldPassword("WrongPassword");
      changePasswordDTO.setNewPassword("NewPassword");

      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());

      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.changeUserPassword(testUser.getId(), testUser.getToken(), changePasswordDTO));
      assertEquals(401, response.getStatus().value());
  }

  @Test
  public void updateUserData_validChangeAll_updateUserData(){
      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setLanguage("en");
      userPutDTO.setLearningLanguage("en");
      userPutDTO.setPrivacy("private");
      userPutDTO.setBirthday("01.01.2000");

      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
      userService.updateUserWithUserId(testUser.getId(), userPutDTO);
      assertEquals(testUser.getLanguage(), userPutDTO.getLanguage());
      assertEquals(testUser.getLearningLanguage(), userPutDTO.getLearningLanguage());
      assertEquals(testUser.getPrivacy(), userPutDTO.getPrivacy());
      assertEquals(testUser.getBirthday(), userPutDTO.getBirthday());
  }

  @Test
  public void updateUserData_languageNullValue_updateCorrectData(){
      testUser.setLanguage("en");

      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setLanguage(null);
      userPutDTO.setLearningLanguage("en");
      userPutDTO.setPrivacy("private");
      userPutDTO.setBirthday("01.01.2000");

      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
      userService.updateUserWithUserId(testUser.getId(), userPutDTO);

      assertEquals("en", testUser.getLanguage());
      assertNotEquals(null, testUser.getLanguage());
      assertEquals(userPutDTO.getLearningLanguage(), testUser.getLearningLanguage());
      assertEquals(userPutDTO.getPrivacy(), testUser.getPrivacy());
      assertEquals(userPutDTO.getBirthday(), testUser.getBirthday());
  }

  @Test
  public void performLogout_validUserId_Success(){
      testUser.setStatus(UserStatus.ONLINE);
      Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());

      userService.performLogout(testUser.getId());

      assertEquals(UserStatus.OFFLINE, testUser.getStatus());

  }

  @Test
  public void verifyLogin_validUserData_Success(){
      User loginUser = new User();
      loginUser.setUsername("testUsername");
      loginUser.setPassword("Password");

      Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);
      userService.verifyLogin(loginUser);
      assertEquals(testUser.getUsername(), loginUser.getUsername());
      assertEquals(testUser.getPassword(), loginUser.getPassword());
      assertEquals(UserStatus.ONLINE, testUser.getStatus());
  }

  @Test
  public void verifyLogin_wrongPassword_throwsException(){
      User loginUser = new User();
      loginUser.setUsername("testUsername");
      loginUser.setPassword("wrongPassword");

      Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);
      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.verifyLogin(loginUser));
      assertEquals(401, response.getStatus().value());
  }

  @Test
  public void verifyLogin_invalidUsername_throwsException(){
      User loginUser = new User();
      loginUser.setUsername("wrongUsername");
      loginUser.setPassword("Password");

      Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);
      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.verifyLogin(loginUser));
      assertEquals(404, response.getStatus().value());
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }


  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void findByUserToken_validToken_success(){
      testUser.setToken("token");

      Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);
      User founduser = userService.findByUserToken(testUser.getToken());
      assertEquals(testUser, founduser);
  }

  @Test
  public void findByUserToken_invalidToken_throwsException(){
      testUser.setToken("invalidToken");

      Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(null);
      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.findByUserToken(testUser.getToken()));
      assertEquals(404, response.getStatus().value());
  }

  @Test
  public void findByUserId_validUserId_success(){
      Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));
      User founduser = userService.findByUserId(testUser.getId());
      assertEquals(testUser, founduser);
  }

  @Test
  public void findByUserId_invalidUserId_throwsException(){
      Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
      ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> userService.findByUserId(testUser.getId()));
      assertEquals(404, response.getStatus().value());
  }

  @Test 
  public void updateUserPhotoValidInput(){

    byte[] imageBytes = "UserPhoto".getBytes();
    MockMultipartFile photo = new MockMultipartFile("userPhoto", imageBytes);

    testUser.setPhoto(null);

    Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());

    userService.updateUserProfilePictureWithUserId(testUser.getId(), photo);
    
    assertNotNull(testUser.getPhoto());
    assertTrue(testUser.getPhoto().startsWith("data:image/png;base64,"));

  }
  @Test
  public void updateUserPhotoInvalidInput() throws Exception{
    MultipartFile photo = Mockito.mock(MultipartFile.class);
    Mockito.doThrow(new IOException("invalid photo format")).when(photo).getBytes();

    Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUserProfilePictureWithUserId(testUser.getId(), photo));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
  }
  @Test
  public void createFriendRequestValidInput(){
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
    Mockito.doReturn(testUser2).when(userService).findByUserToken(Mockito.anyString());

    userService.createFriendRequest(testUser.getId(), testUser2.getToken());

    assertEquals(testUser.getReceivedFriendRequestsList().get(0),2);
    assertEquals(testUser2.getSentFriendRequestsList().get(0),1);

  }

  @Test
  public void createFriendRequestInvalidInputAlreadySent(){
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    testUser.setReceivedFriendRequest(2L);
    testUser2.setSentFriendRequest(1L);

    Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
    Mockito.doReturn(testUser2).when(userService).findByUserToken(Mockito.anyString());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createFriendRequest(testUser.getId(), testUser2.getToken()));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }
  @Test
  public void createFriendRequestInvalidInputUsersAlreadyFriends(){
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    testUser.setFriend(2L);
    testUser2.setFriend(1L);

    testUser.setToken("TestToken");

    Mockito.doReturn(testUser).when(userService).findByUserId(Mockito.anyLong());
    Mockito.doReturn(testUser2).when(userService).findByUserToken(Mockito.anyString());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createFriendRequest(testUser.getId(), testUser2.getToken()));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

  @Test
  public void acceptFriendRequestValidInput(){

    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    testUser.setReceivedFriendRequest(2L);
    testUser2.setSentFriendRequest(1L);

    testUser.setToken("TestToken");

    Chat chat = new Chat();
    Mockito.doReturn(chat).when(chatService).createChat(Mockito.any());

    Mockito.doAnswer(invocation -> {
        Long id = invocation.getArgument(0); 
        return id.equals(1L) ? testUser : testUser2;
    }).when(userService).findByUserId(Mockito.anyLong());


    userService.handleFriendRequest(testUser.getId(), testUser.getToken(), testUser2.getId(), true);

    assertTrue(testUser.getReceivedFriendRequestsList().isEmpty());
    assertTrue(testUser2.getSentFriendRequestsList().isEmpty());
    assertEquals(testUser.getFriendsList().get(0),2L);
    assertEquals(testUser2.getFriendsList().get(0),1L);
  }

  @Test
  public void acceptFriendRequestInvalidInputNotMatchingToken(){
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    testUser.setReceivedFriendRequest(2L);
    testUser2.setSentFriendRequest(1L);

    testUser.setToken("TestToken");

    Mockito.doAnswer(invocation -> {
        Long id = invocation.getArgument(0); 
        return id.equals(1L) ? testUser : testUser2;
    }).when(userService).findByUserId(Mockito.anyLong());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.handleFriendRequest(testUser.getId(), "wrongToken", testUser2.getId(), true));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
  }
  @Test
  public void acceptFriendRequestInvalidInputUsersAlreadyFriends(){
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("Password2");
    testUser2.setUsername("testUsername2");
    testUser2.setToken("TestToken2");

    testUser.setFriend(2L);
    testUser2.setFriend(1L);

    testUser.setToken("TestToken");

    Mockito.doAnswer(invocation -> {
        Long id = invocation.getArgument(0); 
        return id.equals(1L) ? testUser : testUser2;
    }).when(userService).findByUserId(Mockito.anyLong());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.handleFriendRequest(testUser.getId(), testUser.getToken(), testUser2.getId(), true));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }
}
