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
    private Flashcard testFlashcardTrained;

    @BeforeEach
    public void setup(){
  

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("Password");
        testUser.setUsername("testUsername");
        testUser.setLanguage("en");
        testUser.setToken("Token");
        testUser.setLearningLanguage("de");

        testFlashcardSet = new FlashcardSet();
        testFlashcardSet.setFlashcardSetId("SetId");
        testFlashcardSet.setFlashcardSetName("Test");
        testFlashcardSet.setLanguage(testUser.getLanguage());
        testFlashcardSet.setLearningLanguage(testUser.getLearningLanguage());

        testFlashcard = new Flashcard();
        testFlashcard.setFlashcardId("FlashcardId");
        testFlashcard.setContentFront("Hello");
        testFlashcard.setContentBack("Hallo");
        testFlashcard.setLanguage(testFlashcardSet.getLanguage());
        testFlashcard.setLearningLanguage(testFlashcardSet.getLearningLanguage());
        testFlashcard.setStatus(FlashcardStatus.NOTTRAINED);

        testFlashcardTrained = new Flashcard();
        testFlashcardTrained.setFlashcardId("FlashcardId2");
        testFlashcardTrained.setContentFront("Hello2");
        testFlashcardTrained.setContentBack("Hallo2");
        testFlashcardTrained.setLanguage(testFlashcardSet.getLanguage());
        testFlashcardTrained.setLearningLanguage(testFlashcardSet.getLearningLanguage());
        testFlashcardTrained.setStatus(FlashcardStatus.CORRECT);
        
        

        testFlashcardSet.setFlashcardsIds(testFlashcard.getFlashcardId());
        testFlashcardSet.setFlashcardsIds(testFlashcardTrained.getFlashcardId());
        testFlashcard.setFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        testUser.setFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        testFlashcardSet.setUserId(testUser.getId());
        testFlashcard.setUserId(testUser.getId());
        testFlashcardTrained.setUserId(testUser.getId());
        testFlashcardTrained.setFlashcardSetId(testFlashcardSet.getFlashcardSetId());

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(flashcardRepository.save(testFlashcard)).thenReturn(testFlashcard);
        Mockito.when(flashcardRepository.save(testFlashcardTrained)).thenReturn(testFlashcardTrained);
        Mockito.when(flashcardSetRepository.save(Mockito.any())).thenReturn(testFlashcardSet);
    }

    @Test
    public void testGetAllFlashcardSets() throws Exception{
        Mockito.when(flashcardService.findByFlashcardSetId(testFlashcardSet.getFlashcardSetId())).thenReturn(testFlashcardSet);
        Mockito.when(userService.findByUserToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(flashcardService.findByFlashcardId(testFlashcard.getFlashcardId())).thenReturn(testFlashcard);
        Mockito.when(flashcardService.findByFlashcardId(testFlashcardTrained.getFlashcardId())).thenReturn(testFlashcardTrained);
        MockHttpServletRequestBuilder getRequest = get("/flashcards").contentType(MediaType.APPLICATION_JSON).header("Authorization", testUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1))) 
                .andExpect(jsonPath("$[0].flashcardSetName", is(testFlashcardSet.getFlashcardSetName())))
                .andExpect(jsonPath("$[0].flashcardQuantity",is(2)))
                .andExpect(jsonPath("$[0].learningLanguage",is(testFlashcardSet.getLearningLanguage())))
                .andExpect(jsonPath("$[0].flashcardSetId",is(testFlashcardSet.getFlashcardSetId())))
                .andExpect(jsonPath("$[0].language",is(testFlashcardSet.getLanguage())))
                .andExpect(jsonPath("$[0].statistic.NotTrained",is(50.0)))
                .andExpect(jsonPath("$[0].statistic.Wrong",is(0.0)))
                .andExpect(jsonPath("$[0].statistic.Correct",is(50.0)));           
    }
    @Test
    public void testGetAllFlashcardSetsWithNoFLashcards() throws Exception{
        FlashcardSet testFlashcardSet2 = new FlashcardSet();
        testFlashcardSet2.setFlashcardSetId("SetId");
        testFlashcardSet2.setFlashcardSetName("Test");
        testFlashcardSet2.setLanguage(testUser.getLanguage());
        testFlashcardSet2.setLearningLanguage(testUser.getLearningLanguage());
        

        Mockito.when(flashcardService.findByFlashcardSetId(testFlashcardSet2.getFlashcardSetId())).thenReturn(testFlashcardSet2);
        Mockito.when(userService.findByUserToken(testUser.getToken())).thenReturn(testUser);
       
        MockHttpServletRequestBuilder getRequest = get("/flashcards").contentType(MediaType.APPLICATION_JSON).header("Authorization", testUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(1))) 
                .andExpect(jsonPath("$[0].flashcardSetName", is(testFlashcardSet2.getFlashcardSetName())))
                .andExpect(jsonPath("$[0].flashcardQuantity",is(0)))
                .andExpect(jsonPath("$[0].learningLanguage",is(testFlashcardSet2.getLearningLanguage())))
                .andExpect(jsonPath("$[0].flashcardSetId",is(testFlashcardSet2.getFlashcardSetId())))
                .andExpect(jsonPath("$[0].language",is(testFlashcardSet2.getLanguage())))
                .andExpect(jsonPath("$[0].statistic.NotTrained",is(0.0)))
                .andExpect(jsonPath("$[0].statistic.Wrong",is(0.0)))
                .andExpect(jsonPath("$[0].statistic.Correct",is(0.0)));           
    }
    @Test
    public void testGetAllFlashcardFromSet() throws Exception{
        Mockito.when(flashcardService.findByFlashcardSetId(testFlashcardSet.getFlashcardSetId())).thenReturn(testFlashcardSet);
        Mockito.when(userService.findByUserToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(flashcardService.findByFlashcardId(testFlashcard.getFlashcardId())).thenReturn(testFlashcard);
        MockHttpServletRequestBuilder getRequest = get("/flashcards/" + testFlashcardSet.getFlashcardSetId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", testUser.getToken());


        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(2)))
                .andExpect(jsonPath("$[0].flashcardId", is(testFlashcard.getFlashcardId())))
                .andExpect(jsonPath("$[0].learningLanguage", is(testFlashcard.getLearningLanguage())))
                .andExpect(jsonPath("$[0].language", is(testFlashcard.getLanguage())))
                .andExpect(jsonPath("$[0].contentFront", is(testFlashcard.getContentFront())))
                .andExpect(jsonPath("$[0].contentBack", is(testFlashcard.getContentBack())))
                .andExpect(jsonPath("$[0].status").value("NOTTRAINED"));
    }
}
