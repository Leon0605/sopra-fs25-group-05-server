package ch.uzh.ifi.hase.soprafs24.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardSetRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

 
@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest {
  
    @MockBean
    private FlashcardService flashcardService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FlashcardRepository flashcardRepository;

    @MockBean
    private FlashcardSetRepository flashcardSetRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private User testUser;
    private FlashcardSet testFlashcardSet;
    private Flashcard testFlashcard;

    @BeforeEach
    public void setup(){
  

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("Password");
        testUser.setUsername("testUsername");
        testUser.setLanguage("en");
        testUser.setToken("Token");
        testUser.setLearningLanguage("de");

        testFlashcard = new Flashcard();
        testFlashcard.setFlashcardId("FlashcardId");
        testFlashcard.setContentFront("Hello");
        testFlashcard.setContentBack("Hallo");
        testFlashcard.setStatus(FlashcardStatus.NOTTRAINED);
        testFlashcard.setLanguage(testUser.getLanguage());
        testFlashcard.setLearningLanguage(testUser.getLearningLanguage());
        
        testFlashcardSet = new FlashcardSet();
        testFlashcardSet.setFlashcardSetId("SetId");
        testFlashcardSet.setFlashcardSetName("Test");
        testFlashcardSet.setLanguage(testUser.getLanguage());
        testFlashcardSet.setLearningLanguage(testUser.getLearningLanguage());

        testFlashcardSet.setFlashcardsIds(testFlashcard.getFlashcardId());
        testFlashcard.setFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        testUser.setFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        testFlashcardSet.setUserId(testUser.getId());
        testFlashcard.setUserId(testUser.getId());

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(flashcardRepository.save(Mockito.any())).thenReturn(testFlashcard);
        Mockito.when(flashcardSetRepository.save(Mockito.any())).thenReturn(testFlashcardSet);
    }

    @Test
    public void testGetAllFlashcardSets() throws Exception{
        Mockito.when(flashcardService.findByFlashcardSetId(testFlashcardSet.getFlashcardSetId())).thenReturn(testFlashcardSet);
        Mockito.when(userService.findByUserToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(flashcardService.findByFlashcardId(testFlashcard.getFlashcardId())).thenReturn(testFlashcard);
        MockHttpServletRequestBuilder getRequest = get("/flashcards").contentType(MediaType.APPLICATION_JSON).header("Authorization", testUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1))) 
                .andExpect(jsonPath("$[0].flashcardSetName", is(testFlashcardSet.getFlashcardSetName())))
                .andExpect(jsonPath("$[0].flashcardQuantity",is(1)))
                .andExpect(jsonPath("$[0].learningLanguage",is(testFlashcardSet.getLearningLanguage())))
                .andExpect(jsonPath("$[0].flashcardSetId",is(testFlashcardSet.getFlashcardSetId())))
                .andExpect(jsonPath("$[0].language",is(testFlashcardSet.getLanguage())));         
    }
    @Test
    public void testGetAllFlashcardFromSet() throws Exception{
        Mockito.when(flashcardService.findByFlashcardSetId(testFlashcardSet.getFlashcardSetId())).thenReturn(testFlashcardSet);
        Mockito.when(userService.findByUserToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(flashcardService.findByFlashcardId(testFlashcard.getFlashcardId())).thenReturn(testFlashcard);
        MockHttpServletRequestBuilder getRequest = get("/flashcards/" + testFlashcardSet.getFlashcardSetId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", testUser.getToken());


        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1)))
                .andExpect(jsonPath("$[0].flashcardId", is(testFlashcard.getFlashcardId())))
                .andExpect(jsonPath("$[0].learningLanguage", is(testFlashcard.getLearningLanguage())))
                .andExpect(jsonPath("$[0].language", is(testFlashcard.getLanguage())))
                .andExpect(jsonPath("$[0].contentFront", is(testFlashcard.getContentFront())))
                .andExpect(jsonPath("$[0].contentBack", is(testFlashcard.getContentBack())));
    }
}
