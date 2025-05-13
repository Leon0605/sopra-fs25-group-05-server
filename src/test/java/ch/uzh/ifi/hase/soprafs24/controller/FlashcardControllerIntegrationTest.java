package ch.uzh.ifi.hase.soprafs24.controller;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardSetRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcardSet;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@AutoConfigureMockMvc
@SpringBootTest
public class FlashcardControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    
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
  public void testGetFlashcardsSets ()throws Exception{
    IncomingNewFlashcardSet incomingNewFlashcardSet = new IncomingNewFlashcardSet();
    String setName = "SetName";
    incomingNewFlashcardSet.setFlashcardSetName(setName);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    flashcardService.createFlashcardSet(testUser.getToken(), incomingNewFlashcardSet);
    
    FlashcardSet flashcardSet = flashcardSetRepository.findAll().get(0);
    String flashcardSetId = flashcardSet.getFlashcardSetId();

    IncomingNewFlashcard incomingNewFlashcard = new IncomingNewFlashcard();
    String contentFront ="contentFront";
    String contentBack = "contentBack";
    incomingNewFlashcard.setContentFront(contentFront);
    incomingNewFlashcard.setContentBack(contentBack);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);

    MockHttpServletRequestBuilder getRequest = get("/flashcards").contentType(MediaType.APPLICATION_JSON).header("Authorization",testUser.getToken());
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(6)))
                .andExpect(jsonPath("$[0].flashcardSetName").value(setName))
                .andExpect(jsonPath("$[0].flashcardQuantity").value(1))
                .andExpect(jsonPath("$[0].learningLanguage").value(testUser.getLearningLanguage()))
                .andExpect(jsonPath("$[0].flashcardSetId").value(flashcardSetId))
                .andExpect(jsonPath("$[0].language").value(testUser.getLanguage()));
  }
  @Test
  public void testGetFlashcards ()throws Exception{
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
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);
    flashcardService.createFlashcard(flashcardSetId, testUser.getToken(), incomingNewFlashcard);
    Flashcard flashcard = flashcardRepository.findAll().get(0);
    String flashcardId = flashcard.getFlashcardId();

    MockHttpServletRequestBuilder getRequest = get("/flashcards/"+flashcardSetId).contentType(MediaType.APPLICATION_JSON).header("Authorization",testUser.getToken());
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(6)))
                .andExpect(jsonPath("$[0].flashcardId").value(flashcardId))
                .andExpect(jsonPath("$[0].contentFront").value(flashcard.getContentFront()))
                .andExpect(jsonPath("$[0].contentBack").value(flashcard.getContentBack()))
                .andExpect(jsonPath("$[0].language").value(testUser.getLanguage()))
                .andExpect(jsonPath("$[0].learningLanguage").value(testUser.getLearningLanguage()));
  }
}
