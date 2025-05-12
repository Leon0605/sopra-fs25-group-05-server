package ch.uzh.ifi.hase.soprafs24.rest.mapper;


import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.IncomingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;
import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.OutgoingMessage;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.IncomingChatMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.OutgoingMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatDTO.UserChatDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.PhotoMapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.FlashcardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.FlashcardSetGetDTO;

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

@Mapper(uses = { PhotoMapper.class })
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
  @Mapping(source = "learningLanguage", target ="learningLanguage")
  @Mapping(source = "privacy", target ="privacy")
  @Mapping(source = "birthday", target ="birthday")
  @Mapping(source = "photo", target ="photo")
  @Mapping(source = "friendsList", target ="friendsList")
  @Mapping(source = "sentFriendRequestsList", target ="sentFriendRequestsList")
  @Mapping(source = "receivedFriendRequestsList", target ="receivedFriendRequestsList")
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
  @Mapping(source = "timestamp", target = "timestamp")
  @Mapping(source= "status", target = "status")
  OutgoingMessageDTO convertOutgoingMessageToOutgoingMessageDTO(OutgoingMessage outgoingMessage);
    
  @Mapping(source = "flashcardSetName", target = "flashcardSetName")
  @Mapping(source = "learningLanguage", target = "learningLanguage")
  @Mapping(source = "flashcardSetId", target = "flashcardSetId")
  @Mapping(source = "language", target = "language")
  FlashcardSetGetDTO convertFlashcardSetEntityToFlashcardSetGetDTO(FlashcardSet flashcardSet);
  
  @Mapping(source = "flashcardId", target = "flashcardId")
  @Mapping(source = "contentFront", target = "contentFront")
  @Mapping(source = "contentBack", target = "contentBack")
  @Mapping(source = "learningLanguage", target = "learningLanguage")
  @Mapping(source = "language", target = "language")
  FlashcardGetDTO  convertFlashcardEntityToFlashcardGetDTO(Flashcard flashcard);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "photo", target = "photo")
  UserGetAllDTO convertEntityToUserGetAllDTO(User user);
}
