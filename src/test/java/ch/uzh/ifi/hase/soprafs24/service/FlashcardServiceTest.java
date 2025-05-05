package ch.uzh.ifi.hase.soprafs24.service;



import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories.FlashcardSetRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcardSet;
import ch.uzh.ifi.hase.soprafs24.service.API.AzureAPI;

public class FlashcardServiceTest {



    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private FlashcardSetRepository flashcardSetRepository;

    @InjectMocks
    @Spy
    private FlashcardService flashcardService;

    private User testUser;
    private FlashcardSet testFlashcardSet;
    private Flashcard testFlashcard;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

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
    public void testValidFlashcardSetCreation(){
        String token = testUser.getToken();
        IncomingNewFlashcardSet dto = new IncomingNewFlashcardSet();
        dto.setFlashcardSetName("TestSet");

        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.when(flashcardService.generateId()).thenReturn("SetId2");

        flashcardService.createFlashcardSet(token, dto);
        Mockito.verify(flashcardSetRepository, times(1)).save(Mockito.any(FlashcardSet.class));
        Mockito.verify(userRepository, times(1)).save(testUser);
        assertEquals("SetId2", testUser.getFlashcardSetsIds().get(1));

    }
    @Test 
    public void testValidfindByFlashcardSetId(){
        Mockito.when(flashcardSetRepository.findByFlashcardSetId(Mockito.any())).thenReturn(testFlashcardSet);

        FlashcardSet output = flashcardService.findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());

        assertEquals(output.getFlashcardSetName(), testFlashcardSet.getFlashcardSetName());
        assertEquals(output.getFlashcardSetId(), testFlashcardSet.getFlashcardSetId());
        assertEquals(output.getFlashcardsIds(),testFlashcardSet.getFlashcardsIds());
        assertEquals(output.getLanguage(),testFlashcardSet.getLanguage());
        assertEquals(output.getLearningLanguage(),testFlashcardSet.getLearningLanguage());
        assertEquals(output.getUserId(), testFlashcardSet.getUserId());
    }

    @Test
    public void testInvalidFindByFlashcardSetId(){
        Mockito.when(flashcardSetRepository.findByFlashcardSetId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> flashcardService.findByFlashcardSetId("wrong"));
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    public void testValidFindByFlashcardId(){
        Mockito.when(flashcardRepository.findByFlashcardId(Mockito.any())).thenReturn(testFlashcard);

        Flashcard output = flashcardService.findByFlashcardId(testFlashcard.getFlashcardId());

        assertEquals(output.getContentBack(), testFlashcard.getContentBack());
        assertEquals(output.getFlashcardSetId(), testFlashcard.getFlashcardSetId());
        assertEquals(output.getContentFront(),testFlashcard.getContentFront());
        assertEquals(output.getLanguage(),testFlashcard.getLanguage());
        assertEquals(output.getLearningLanguage(),testFlashcard.getLearningLanguage());
        assertEquals(output.getUserId(), testFlashcard.getUserId());
        assertEquals(output.getFlashcardSetId(), testFlashcard.getFlashcardSetId());
    }

    @Test
    public void testInvalidFindByFlashcardId(){
        Mockito.when(flashcardRepository.findByFlashcardId(Mockito.any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> flashcardService.findByFlashcardId("wrong"));
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    public void testCreateFlashcardWithNoAzureTranslation(){
        String token = testUser.getToken();
        IncomingNewFlashcard dto = new IncomingNewFlashcard();

        dto.setContentFront("This is a Test");
        dto.setContentBack("Das ist ein Test");

        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.when(flashcardService.generateId()).thenReturn("FlashcardId2");

 
        flashcardService.createFlashcard(testFlashcardSet.getFlashcardSetId(), token, dto);

         
        ArgumentCaptor<Flashcard> captor = ArgumentCaptor.forClass(Flashcard.class);
        Mockito.verify(flashcardRepository, times(1)).save(captor.capture());
        Flashcard createdFlashcard = captor.getValue();


        assertEquals(createdFlashcard.getContentBack(), dto.getContentBack());
        assertEquals(createdFlashcard.getContentFront(), dto.getContentFront());
        assertEquals(createdFlashcard.getFlashcardId(),"FlashcardId2");
        assertEquals(createdFlashcard.getFlashcardSetId(), testFlashcardSet.getFlashcardSetId());
        assertEquals(createdFlashcard.getLanguage(), testFlashcardSet.getLanguage());
        assertEquals(createdFlashcard.getLearningLanguage(),testFlashcardSet.getLearningLanguage());
        assertEquals(createdFlashcard.getUserId(),testUser.getId());
    }
    @Test
    public void testCreateFlashcardWithAzureTranslation(){
        String token = testUser.getToken();
        IncomingNewFlashcard dto = new IncomingNewFlashcard();

        dto.setContentFront("This is a Test");


        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.when(flashcardService.generateId()).thenReturn("FlashcardId2");
            
        try (MockedStatic<AzureAPI> azureMock = mockStatic(AzureAPI.class)) {
            azureMock.when(() ->AzureAPI.AzureTranslate(dto.getContentFront(),testUser.getLanguage(),testUser.getLearningLanguage())).thenReturn("Das ist ein Test");
            ArgumentCaptor<Flashcard> captor = ArgumentCaptor.forClass(Flashcard.class);

            flashcardService.createFlashcard(testFlashcardSet.getFlashcardSetId(), token, dto);

            Mockito.verify(flashcardRepository, times(1)).save(captor.capture());
            
            Flashcard createdFlashcard = captor.getValue();

            assertEquals(createdFlashcard.getContentBack(), "Das ist ein Test");
            assertEquals(createdFlashcard.getContentFront(), dto.getContentFront());
            assertEquals(createdFlashcard.getFlashcardId(),"FlashcardId2");
            assertEquals(createdFlashcard.getFlashcardSetId(), testFlashcardSet.getFlashcardSetId());
            assertEquals(createdFlashcard.getLanguage(), testFlashcardSet.getLanguage());
            assertEquals(createdFlashcard.getLearningLanguage(),testFlashcardSet.getLearningLanguage());
            assertEquals(createdFlashcard.getUserId(),testUser.getId());
        }        
    }
    @Test
    public void testUpdateFlashcardWithNoAzureTranslation(){
        String token = testUser.getToken();
        IncomingNewFlashcard dto = new IncomingNewFlashcard();

        dto.setContentFront("This is a Test");
        dto.setContentBack("Das ist ein Test");

        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.doReturn(testFlashcard).when(flashcardService).findByFlashcardId(testFlashcard.getFlashcardId());

        
        flashcardService.updateFlashcard(testUser.getToken(), testFlashcardSet.getFlashcardSetId(), testFlashcard.getFlashcardId(), dto);

        ArgumentCaptor<Flashcard> captor = ArgumentCaptor.forClass(Flashcard.class);
        Mockito.verify(flashcardRepository, times(1)).save(captor.capture());
        Flashcard createdFlashcard = captor.getValue();

        assertEquals(createdFlashcard.getContentBack(), dto.getContentBack());
        assertEquals(createdFlashcard.getContentFront(), dto.getContentFront());

    }
    @Test
    public void testUpdateFlashcardWithAzureTranslation(){
        String token = testUser.getToken();
        IncomingNewFlashcard dto = new IncomingNewFlashcard();

        dto.setContentFront("This is a Test");
      

        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.doReturn(testFlashcard).when(flashcardService).findByFlashcardId(testFlashcard.getFlashcardId());

        
    
        try (MockedStatic<AzureAPI> azureMock = mockStatic(AzureAPI.class)) {
            azureMock.when(() ->AzureAPI.AzureTranslate(dto.getContentFront(),testUser.getLanguage(),testUser.getLearningLanguage())).thenReturn("Das ist ein Test");
            ArgumentCaptor<Flashcard> captor = ArgumentCaptor.forClass(Flashcard.class);

            flashcardService.updateFlashcard(testUser.getToken(), testFlashcardSet.getFlashcardSetId(), testFlashcard.getFlashcardId(), dto);

            Mockito.verify(flashcardRepository, times(1)).save(captor.capture());
            
            Flashcard createdFlashcard = captor.getValue();
            assertEquals(createdFlashcard.getContentBack(), "Das ist ein Test");
            assertEquals(createdFlashcard.getContentFront(), dto.getContentFront());

        }
    }
    @Test
    public void testDeleteFlashcard(){
        String token = testUser.getToken();
    
        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.doReturn(testFlashcard).when(flashcardService).findByFlashcardId(testFlashcard.getFlashcardId());

        flashcardService.deleteFlashcard(token, testFlashcardSet.getFlashcardSetId(), testFlashcard.getFlashcardId());
        Mockito.verify(flashcardRepository, times(1)).delete(testFlashcard);
        Mockito.verify(flashcardSetRepository, times(1)).save(testFlashcardSet);
        assertTrue(testFlashcardSet.getFlashcardsIds().isEmpty());
    }
    @Test
    public void testDeleteFlashcardSet(){
        String token = testUser.getToken();
    
        Mockito.when(userService.findByUserToken(token)).thenReturn(testUser);
        Mockito.doReturn(testFlashcardSet).when(flashcardService).findByFlashcardSetId(testFlashcardSet.getFlashcardSetId());
        Mockito.doReturn(testFlashcard).when(flashcardService).findByFlashcardId(testFlashcard.getFlashcardId());

        flashcardService.deleteFlashcardSet(token, testFlashcardSet.getFlashcardSetId());

        Mockito.verify(flashcardRepository, times(1)).delete(testFlashcard);
        Mockito.verify(flashcardSetRepository, times(1)).delete(testFlashcardSet);
        Mockito.verify(userRepository, times(1)).save(testUser);
        assertTrue(testUser.getFlashcardSetsIds().isEmpty());
    }
    @Test
    public void testGenerateId(){
        String id = flashcardService.generateId();

        assertEquals(id.getClass(), String.class);
    }
}
