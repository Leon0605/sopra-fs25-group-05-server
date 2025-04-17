package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.FlashcardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.FlashcardSetGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO.IncomingNewFlashcardSet;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@RestController
public class FlashcardController {
    
    private final FlashcardService flashcardService;
    private final UserService userService;
    @Autowired
    FlashcardController(FlashcardService flashcardService, UserService userService){
        this.userService = userService;
        this.flashcardService = flashcardService;
    }

    @PostMapping("/flashcards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createFlashcardSet(@RequestHeader("Authorization") String userToken, @RequestBody IncomingNewFlashcardSet incomingNewFlashcardSet){
        flashcardService.createFlashcardSet(userToken,incomingNewFlashcardSet);
    }

    @PostMapping("/flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createFlashcard(@PathVariable String flashcardSetId, @RequestHeader("Authorization") String userToken,@RequestBody IncomingNewFlashcard incomingNewFlashcard){
        flashcardService.createFlashcard(flashcardSetId, userToken, incomingNewFlashcard);
    }

    @GetMapping("/flashcards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<FlashcardSetGetDTO> getAllFlashcardSets(@RequestHeader("Authorization") String userToken){
        User user = userService.findByUserToken(userToken);
        ArrayList<FlashcardSetGetDTO> flashcardSetsGetDTOs = new ArrayList<>();

        for(String flashcardSetId : user.getFlashcardSetsIds()){
            FlashcardSet flashcardSet = flashcardService.findByFlashcardSetId(flashcardSetId);
            FlashcardSetGetDTO flashcardSetGetDTO = DTOMapper.INSTANCE.convertFlashcardSetEntityToFlashcardSetGetDTO(flashcardSet);

            int flashcardQuantity = flashcardSet.getFlashcardsIds().size();
            flashcardSetGetDTO.setFlashcardQuantity(flashcardQuantity);

            flashcardSetsGetDTOs.add(flashcardSetGetDTO);
        }
        return flashcardSetsGetDTOs;
        
    }
    @GetMapping("/flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<FlashcardGetDTO> getAllFlashcardInSet(@PathVariable String flashcardSetId){
        
        FlashcardSet flashcardSet = flashcardService.findByFlashcardSetId(flashcardSetId);

        ArrayList<FlashcardGetDTO> flashcardGetDTOs = new ArrayList<>();

        for(String flashcardId : flashcardSet.getFlashcardsIds()){
            Flashcard flashcard = flashcardService.findByFlashcardId(flashcardId);
            flashcardGetDTOs.add(DTOMapper.INSTANCE.convertFlashcardEntityToFlashcardGetDTO(flashcard));
            
        }
        return flashcardGetDTOs;
    }
}
