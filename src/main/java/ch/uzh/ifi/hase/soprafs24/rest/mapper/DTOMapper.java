package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserChatDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.IncomingChatMessageDTO;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;

import ch.uzh.ifi.hase.soprafs24.rest.dto.OutgoingMessageDTO;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "id", target = "id")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "language", target ="language")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "token", target = "token")
  UserLoginDTO convertEntityToUserLoginDTO(User user);

  @Mapping(source = "userIds", target = "userIds")
  @Mapping(source = "chatId", target = "chatId")
  UserChatDTO convertChatEntityToUserChatDTO(Chat chat);


  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "messageId", target = "messageId")
  @Mapping(source = "languageMapping", target = "languageMapping")
  
  ChatMessageDTO convertMessageEntityToChatMessageDTO(Message message);
  //Message convertMessageDTOToMessageEntity(ChatMessageDTO messageDTO);



  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "content", target = "content")
  IncomingMessage convertIncomingChatMessageDTOToIncomingMessage(IncomingChatMessageDTO incomingChatMessageDTO);


  @Mapping(source = "userId", target = "userId")
    @Mapping(source = "messageId", target="messageId")
    @Mapping(source = "chatId", target = "chatId")
    @Mapping(source = "originalMessage", target = "originalMessage")
    @Mapping(source = "translatedMessage", target = "translatedMessage")
    OutgoingMessageDTO convertOutgoingMessageToOutgoingMessageDTO(OutgoingMessage outgoingMessage);
}
