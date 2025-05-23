package ch.uzh.ifi.hase.soprafs24.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

        flashcardSet.setFlashcardSetId(generateId());
        flashcardSet.setUserId(user.getId());
        flashcardSet.setLearningLanguage(user.getLearningLanguage());
        flashcardSet.setLanguage(user.getLanguage());
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
        flashcard.setFlashcardId(generateId());
        flashcard.setFlashcardSetId(flashcardSetId);
        flashcard.setUserId(user.getId());
        flashcard.setLearningLanguage(flashcardSet.getLearningLanguage());
        flashcard.setLanguage(flashcardSet.getLanguage());
        flashcard.setContentFront(incomingNewFlashcard.getContentFront());
        flashcard.setStatus(FlashcardStatus.NOTTRAINED);

        if(incomingNewFlashcard.getContentBack() != null){
            flashcard.setContentBack(incomingNewFlashcard.getContentBack());
            flashcard.setContentFront(incomingNewFlashcard.getContentFront());
        }
        else{
        flashcard.setContentBack(AzureAPI.AzureTranslate(incomingNewFlashcard.getContentFront(), flashcardSet.getLanguage(), flashcardSet.getLearningLanguage()));
        }

        flashcardRepository.save(flashcard);
        flashcardRepository.flush();
        
        flashcardSet.setFlashcardsIds(flashcard.getFlashcardId());
        flashcardSetRepository.save(flashcardSet);
        flashcardSetRepository.flush();
        
    }

    public void updateFlashcard(String userToken,String FlashcardSetId, String FlashcardId,IncomingNewFlashcard incomingNewFlashcard){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(FlashcardSetId);
        Flashcard flashcard = findByFlashcardId(FlashcardId);

        if(incomingNewFlashcard.getContentBack() != null){
            flashcard.setContentBack(incomingNewFlashcard.getContentBack());
            flashcard.setContentFront(incomingNewFlashcard.getContentFront());
        }
        else{
            flashcard.setContentFront(incomingNewFlashcard.getContentFront());
            String contentBack = AzureAPI.AzureTranslate(incomingNewFlashcard.getContentFront(), flashcard.getLanguage() , flashcard.getLearningLanguage());
            flashcard.setContentBack(contentBack);
        }
        flashcard.setStatus(FlashcardStatus.NOTTRAINED);
        flashcardRepository.save(flashcard);
        flashcardRepository.flush();

    }
    public void updateFlashcardSetName(String userToken,String flashcardSetId, IncomingNewFlashcardSet IncommingNewFlashcardSet){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);
        flashcardSet.setFlashcardSetName(IncommingNewFlashcardSet.getFlashcardSetName());
        flashcardSetRepository.save(flashcardSet);
        flashcardSetRepository.flush();
    }
    public void deleteFlashcard(String userToken,String flashcardSetId,String flashcardId){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);
        Flashcard flashcard = findByFlashcardId(flashcardId);

        flashcardRepository.delete(flashcard);

        flashcardSet.getFlashcardsIds().remove(flashcard.getFlashcardId());

        flashcardSetRepository.save(flashcardSet);
        flashcardSetRepository.flush();
    }
    
    public void deleteFlashcardSet(String userToken, String flashcardSetId){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);

        for(String flashcardId : flashcardSet.getFlashcardsIds()){
            Flashcard flashcard = findByFlashcardId(flashcardId);
            flashcardRepository.delete(flashcard);
        }
        
        flashcardSetRepository.delete(flashcardSet);

        user.getFlashcardSetsIds().remove(flashcardSetId);

        userRepository.save(user);
        userRepository.flush();
    }
    public void updateFlashcardStatus(String userToken, boolean status, String flashcardSetId,String flashcardId){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);
        Flashcard flashcard = findByFlashcardId(flashcardId);

        if(status)flashcard.setStatus(FlashcardStatus.CORRECT);
        else flashcard.setStatus(FlashcardStatus.WRONG);

        flashcardRepository.save(flashcard);
        flashcardRepository.flush();
   
    }
    public void resetFlashcardStatus(String userToken, String flashcardSetId){
        User user = userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = findByFlashcardSetId(flashcardSetId);

        for(String flashcardId : flashcardSet.getFlashcardsIds()){
            
            Flashcard flashcard = findByFlashcardId(flashcardId);
            flashcard.setStatus(FlashcardStatus.NOTTRAINED);
            flashcardRepository.save(flashcard);
            flashcardRepository.flush();
        }
    }
    public String generateId(){
        return UUID.randomUUID().toString();
    }
}
