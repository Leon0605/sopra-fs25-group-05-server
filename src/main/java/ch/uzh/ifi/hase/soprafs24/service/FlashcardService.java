package ch.uzh.ifi.hase.soprafs24.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

@Service
@Transactional
public class FlashcardService {
    private final UserService userService;
    private final FlashcardSetRepository flashcardSetRepository;
    private final UserRepository userRepository;
    private final FlashcardRepository flashcardRepository;

    FlashcardService(UserService userService, FlashcardSetRepository flashcardSetRepository, UserRepository userRepository,FlashcardRepository flashcardRepository){
        this.userService = userService;
        this.flashcardSetRepository = flashcardSetRepository;
        this.userRepository = userRepository;
        this.flashcardRepository = flashcardRepository;
    }
    public void createFlashcardSet(String userToken,IncomingNewFlashcardSet incomingNewFlashcardSet){
        User user = userService.findByUserToken(userToken);
        
        FlashcardSet flashcardSet = new FlashcardSet();

        flashcardSet.setFlashcardSetId(UUID.randomUUID().toString());
        flashcardSet.setUserId(user.getId());
        flashcardSet.setLearningLanguage(user.getLearningLanguage());
        flashcardSet.setFlashcardSetName(incomingNewFlashcardSet.getFlashcardSetName());
        flashcardSetRepository.save(flashcardSet);
        flashcardSetRepository.flush();

        user.setFlashcardSetId(flashcardSet.getFlashcardSetId());  
        userRepository.save(user);
        userRepository.flush();
    }
    public FlashcardSet findByFlashcardSetId(String flashcardSetId){
        FlashcardSet flashcardSet = flashcardSetRepository.findByFlashcardSetId(flashcardSetId);
        if(flashcardSet == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FlashcardSet not found");
        }
        return flashcardSet;
    }
    public Flashcard findByFlashcardId(String flashcardId){
        Flashcard flashcard = flashcardRepository.findByFlashcardId(flashcardId);
        if(flashcard == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flashcard not found");
        }
        return flashcard;
    }
    public void createFlashcard(String flashcardSetId, String userToken, IncomingNewFlashcard incomingNewFlashcard){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);

        Flashcard flashcard = new Flashcard();
        flashcard.setFlashcardId(UUID.randomUUID().toString());
        flashcard.setFlashcardSetId(flashcardSetId);
        flashcard.setUserId(user.getId());
        flashcard.setLearningLanguage(user.getLearningLanguage());
        flashcard.setContentFront(incomingNewFlashcard.getContentFront());
        flashcard.setContentBack(AzureAPI.AzureTranslate(incomingNewFlashcard.getContentFront(), user.getLanguage(), user.getLearningLanguage()));
        flashcardRepository.saveAndFlush(flashcard);

        flashcardSet.setFlashcardsIds(flashcard.getFlashcardId());
        flashcardSetRepository.saveAndFlush(flashcardSet);
        
    }
}
