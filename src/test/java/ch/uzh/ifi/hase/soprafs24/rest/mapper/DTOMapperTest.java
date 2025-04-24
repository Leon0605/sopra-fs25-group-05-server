package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.IncomingChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.OutgoingMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.UserChatDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserLoginDTO;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;

import java.util.ArrayList;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Password");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void fromUser_toUserLoginDto_success() {
      User user = new User();
      user.setUsername("firstname@lastname");
      user.setStatus(UserStatus.OFFLINE);
      user.setToken("1");
      user.setId(1L);

      UserLoginDTO userLoginDTO = DTOMapper.INSTANCE.convertEntityToUserLoginDTO(user);
      assertEquals(user.getToken(), userLoginDTO.getToken());
  }

  @Test
  public void fromChat_toUserChatDTO_success() {
      User user1 = new User();
      user1.setId(1L);

      User user2 = new User();
      user2.setId(2L);

      ArrayList<Long> userIds = new ArrayList<>();
      userIds.add(user1.getId());
      userIds.add(user2.getId());

      Chat chat = new Chat();
      chat.setChatId("chatId");
      chat.setUserIds(userIds);

      UserChatDTO userChatDto = DTOMapper.INSTANCE.convertChatEntityToUserChatDTO(chat);
      assertEquals(userChatDto.getChatId(), chat.getChatId());
      assertEquals(userChatDto.getUserIds(), userIds);
  }

  @Test
  public void fromIncomingChatMessageDTO_toIncomingChatMessage_success(){
      IncomingChatMessageDTO incomingChatMessageDTO = new IncomingChatMessageDTO();
      incomingChatMessageDTO.setChatId("chatId");
      incomingChatMessageDTO.setUserId(1L);
      incomingChatMessageDTO.setContent("test content");

      IncomingMessage incomingMessage = DTOMapper.INSTANCE.convertIncomingChatMessageDTOToIncomingMessage(incomingChatMessageDTO);
      assertEquals(incomingChatMessageDTO.getChatId(), incomingMessage.getChatId());
      assertEquals(incomingChatMessageDTO.getUserId(), incomingMessage.getUserId());
      assertEquals(incomingChatMessageDTO.getContent(), incomingMessage.getContent());
  }

  @Test
  public void fromOutgoingChatMessage_toOutgoingChatMessageDTO_success(){
      OutgoingMessage outgoingMessage = new OutgoingMessage();
      outgoingMessage.setChatId("chatId");
      outgoingMessage.setUserId(1L);
      outgoingMessage.setMessageId("messageId");
      outgoingMessage.setTimestamp("Timestamp");
      outgoingMessage.setOriginalMessage("Hello");
      outgoingMessage.setTranslatedMessage("Translated");

      OutgoingMessageDTO outgoingMessageDTO = DTOMapper.INSTANCE.convertOutgoingMessageToOutgoingMessageDTO(outgoingMessage);
      assertEquals(outgoingMessageDTO.getChatId(), outgoingMessage.getChatId());
      assertEquals(outgoingMessageDTO.getUserId(), outgoingMessage.getUserId());
      assertEquals(outgoingMessageDTO.getMessageId(), outgoingMessage.getMessageId());
      assertEquals(outgoingMessageDTO.getTimestamp(), outgoingMessage.getTimestamp());
      assertEquals(outgoingMessageDTO.getOriginalMessage(), outgoingMessage.getOriginalMessage());
      assertEquals(outgoingMessageDTO.getTranslatedMessage(), outgoingMessage.getTranslatedMessage());
  }

}
