package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserChangePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  @Spy
  private UserService userService;

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

}
