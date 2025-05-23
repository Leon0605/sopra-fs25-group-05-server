package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardSetRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcardSet;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;

@WebAppConfiguration
@SpringBootTest
public class FlashcardServiceIntegrationTest {
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

  
    @Autowired
    private FlashcardRepository flashcardRepository;

    
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private FlashcardService flashcardService;
    
    User testUser;

    @BeforeEach
    public void setup() {
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setPassword("Password");
        user.setUsername("testUsername");
        testUser = userService.createUser(user);
        testUser.setLearningLanguage("de");
        userRepository.save(testUser);
        userRepository.flush();
  }

  @Test
  public void testFlashcardSetCreation(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    
    assertTrue(testUser.getFlashcardSetsIds().isEmpty());
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);

    User updated = userService.findByUserToken(testUser.getToken());
    assertFalse(updated.getFlashcardSetsIds().isEmpty());

    List<FlashcardSet> allFlashcardSets = flashcardSetRepository.findAll();
    
    assertEquals(allFlashcardSets.get(0).getFlashcardSetName(),setName);
    assertEquals(testUser.getLearningLanguage(),allFlashcardSets.get(0).getLearningLanguage() );
    assertEquals(testUser.getLanguage(),allFlashcardSets.get(0).getLanguage() );
  }
  @Test
  public void testFindByFlashcardSetIdValid(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);

    
    List<FlashcardSet> flashcardSets = flashcardSetRepository.findAll();

    FlashcardSet foundFlashcardSet = flashcardService.findByFlashcardSetId(flashcardSets.get(0).getFlashcardSetId());
    assertNotNull(foundFlashcardSet);
    assertEquals(setName, foundFlashcardSet.getFlashcardSetName());
  }
  @Test
  public void testFindByFlashcardSetIdInvalid(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> flashcardService.findByFlashcardSetId("wrongId"));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }
  
  @Test
  public void testCreateFlashcardWithNoAzure(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    Flashcard flashcard = flashcardRepository.findAll().get(0);

    assertEquals(contentBack, flashcard.getContentBack());
    assertEquals(contentFront,flashcard.getContentFront());
    assertEquals(flashcardSet.getLanguage(),flashcard.getLanguage());
    assertEquals(flashcardSet.getLearningLanguage(),flashcard.getLearningLanguage());
    assertEquals(flashcard.getStatus(), FlashcardStatus.NOTTRAINED);
  }
  @Test
  public void testCreateFlashcardWithAzure(){

    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    incomingNewFlashcard.setContentFront(contentFront);
    String azureReturn = "contentBackAzure";

    try (MockedStatic<AzureAPI> azureMock = mockStatic(AzureAPI.class)) {
        azureMock.when(() -> AzureAPI.AzureTranslate(anyString(), anyString(), anyString())).thenReturn(azureReturn);
        flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

        Flashcard flashcard = flashcardRepository.findAll().get(0);
             
        assertEquals(azureReturn, flashcard.getContentBack());
        assertEquals(contentFront,flashcard.getContentFront());
        assertEquals(flashcardSet.getLanguage(),flashcard.getLanguage());
        assertEquals(flashcardSet.getLearningLanguage(),flashcard.getLearningLanguage());
        assertEquals(flashcard.getStatus(), FlashcardStatus.NOTTRAINED);
    }
  }
  @Test
  public void testFindByFlashcardIdValid(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    String flashcardId = flashcardRepository.findAll().get(0).getFlashcardId();

    Flashcard foundFlashcard = flashcardService.findByFlashcardId(flashcardId);

    assertNotNull(foundFlashcard);
    assertEquals(contentBack, foundFlashcard.getContentBack());
    assertEquals(contentFront,foundFlashcard.getContentFront());
    assertEquals(flashcardSet.getLanguage(),foundFlashcard.getLanguage());
    assertEquals(flashcardSet.getLearningLanguage(),foundFlashcard.getLearningLanguage());
  }
  @Test
  public void testFindByFlashcardIdInvalid(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();


    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> flashcardService.findByFlashcardId("wrongId"));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }
  @Test
  public void updateFlashcardWithNoAzure(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    Flashcard flashcard = flashcardRepository.findAll().get(0);
    flashcard.setStatus(FlashcardStatus.CORRECT);
    flashcardRepository.save(flashcard);

    flashcard = flashcardRepository.findAll().get(0);

    assertEquals(contentBack, flashcard.getContentBack());
    assertEquals(contentFront,flashcard.getContentFront());
    assertEquals(flashcard.getStatus(), FlashcardStatus.CORRECT);

    IncomingNewFlashcard incomingNewFlashcardUpdate = new IncomingNewFlashcard();
    String updatedContentFront ="contentFrontUpdated";
    String updatedContentBack = "contentBackUpdated";

    incomingNewFlashcardUpdate.setContentBack(updatedContentBack);
    incomingNewFlashcardUpdate.setContentFront(updatedContentFront);
    
    flashcardService.updateFlashcard(testUser.getToken(), flashcardSetId, flashcard.getFlashcardId(), incomingNewFlashcardUpdate);

    Flashcard updatedFlashcard = flashcardRepository.findAll().get(0);

    
    assertEquals(updatedContentBack, updatedFlashcard.getContentBack());
    assertEquals(updatedContentFront,updatedFlashcard.getContentFront());
    assertEquals(updatedFlashcard.getStatus(), FlashcardStatus.NOTTRAINED);
  }
  @Test
  public void updateFlashcardWithAzure(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    Flashcard flashcard = flashcardRepository.findAll().get(0);
    flashcard.setStatus(FlashcardStatus.CORRECT);
    flashcardRepository.save(flashcard);

    flashcard = flashcardRepository.findAll().get(0);

    assertEquals(contentBack, flashcard.getContentBack());
    assertEquals(contentFront,flashcard.getContentFront());
    assertEquals(flashcard.getStatus(), FlashcardStatus.CORRECT);

    IncomingNewFlashcard incomingNewFlashcardUpdate = new IncomingNewFlashcard();
    String updatedContentFront ="contentFront";
    String azureReturn = "contentBackAzureUpdated";
    incomingNewFlashcardUpdate.setContentFront(updatedContentFront);

    try (MockedStatic<AzureAPI> azureMock = mockStatic(AzureAPI.class)) {
        azureMock.when(() -> AzureAPI.AzureTranslate(anyString(), anyString(), anyString())).thenReturn(azureReturn);
        flashcardService.updateFlashcard(testUser.getToken(), flashcardSetId, flashcard.getFlashcardId(), incomingNewFlashcardUpdate);

        Flashcard updatedFlashcard = flashcardRepository.findAll().get(0);

        
        assertEquals(azureReturn, updatedFlashcard.getContentBack());
        assertEquals(updatedContentFront,updatedFlashcard.getContentFront());
        assertEquals(updatedFlashcard.getStatus(), FlashcardStatus.NOTTRAINED);
    }
  }
  @Test
  public void testUpdateFlashcardSetName(){

    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);

    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);

    assertEquals(setName, flashcardSet.getFlashcardSetName());

    IncomingNewFlashcardSet incomingNewFlashcardSetUpdate = new IncomingNewFlashcardSet();
    String updatedSetName = "SetNameUpdate";
    incomingNewFlashcardSetUpdate.setFlashcardSetName(updatedSetName);
    flashcardService.updateFlashcardSetName(testUser.getToken(),flashcardSet.getFlashcardSetId(), incomingNewFlashcardSetUpdate);

    FlashcardSet updatedFlashcardSet = flashcardSetRepository.findAll().get(0);
    assertEquals(updatedSetName, updatedFlashcardSet.getFlashcardSetName());
  }
  @Test
  public void testDeleteFlashcard(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    assertFalse(flashcardRepository.findAll().isEmpty());
    assertFalse(flashcardSetRepository.findAll().get(0).getFlashcardsIds().isEmpty());

    Flashcard flashcard = flashcardRepository.findAll().get(0);
    flashcardService.deleteFlashcard(testUser.getToken(), flashcardSetId, flashcard.getFlashcardId());

    assertTrue(flashcardRepository.findAll().isEmpty());
    assertTrue(flashcardSetRepository.findAll().get(0).getFlashcardsIds().isEmpty());
   
  }
  @Test
  public void testDeleteFlashcardSet(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    assertFalse(flashcardRepository.findAll().isEmpty());
    assertFalse(flashcardSetRepository.findAll().isEmpty());
    assertFalse(userRepository.findAll().get(0).getFlashcardSetsIds().isEmpty());

    flashcardService.deleteFlashcardSet(testUser.getToken(), flashcardSetId);

    assertTrue(flashcardRepository.findAll().isEmpty());
    assertTrue(flashcardSetRepository.findAll().isEmpty());
    assertTrue(userRepository.findAll().get(0).getFlashcardSetsIds().isEmpty());
  }
  @Test
  public void testUpdatedFlashcardStatusFromNOTTRAINEDToCORRECT(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    Flashcard flashcard = flashcardRepository.findAll().get(0);
    assertEquals(flashcard.getStatus(), FlashcardStatus.NOTTRAINED);

    flashcardService.updateFlashcardStatus(testUser.getToken(), true, flashcardSetId, flashcard.getFlashcardId());
    Flashcard updatedFlashcard = flashcardRepository.findAll().get(0);
    assertEquals(updatedFlashcard.getStatus(), FlashcardStatus.CORRECT);
  }
  @Test
  public void testUpdatedFlashcardStatusFromNOTTRAINEDToWRONG(){
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    Flashcard flashcard = flashcardRepository.findAll().get(0);
    assertEquals(flashcard.getStatus(), FlashcardStatus.NOTTRAINED);

    flashcardService.updateFlashcardStatus(testUser.getToken(), false, flashcardSetId, flashcard.getFlashcardId());
    Flashcard updatedFlashcard = flashcardRepository.findAll().get(0);
    assertEquals(updatedFlashcard.getStatus(), FlashcardStatus.WRONG);
  }

}
