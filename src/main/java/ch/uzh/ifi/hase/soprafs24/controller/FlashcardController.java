package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardStatus;
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

    //add a new set
    @PostMapping("/flashcards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createFlashcardSet(@RequestHeader("Authorization") String userToken, @RequestBody IncomingNewFlashcardSet incomingNewFlashcardSet){
        flashcardService.createFlashcardSet(userToken,incomingNewFlashcardSet);
    }
    //add a flashcard to a set with just contentFront (contentBack translated automatically) or with contentFront and contentBack manually
    @PostMapping("/flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createFlashcard(@PathVariable String flashcardSetId, @RequestHeader("Authorization") String userToken,@RequestBody IncomingNewFlashcard incomingNewFlashcard){
        flashcardService.createFlashcard(flashcardSetId, userToken, incomingNewFlashcard);
    }

    //get all flashcardSet
    @GetMapping("/flashcards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<FlashcardSetGetDTO> getAllFlashcardSets(@RequestHeader("Authorization") String userToken){
        User user = userService.findByUserToken(userToken);
        ArrayList<FlashcardSetGetDTO> flashcardSetsGetDTOs = new ArrayList<>();
        
        

        for(String flashcardSetId : user.getFlashcardSetsIds()){
            FlashcardSet flashcardSet = flashcardService.findByFlashcardSetId(flashcardSetId);
            FlashcardSetGetDTO flashcardSetGetDTO = DTOMapper.INSTANCE.convertFlashcardSetEntityToFlashcardSetGetDTO(flashcardSet);

            
            ArrayList<String> flashcardIds = flashcardSet.getFlashcardsIds();
            int flashcardQuantity = flashcardIds.size();
            flashcardSetGetDTO.setFlashcardQuantity(flashcardQuantity);

            if(flashcardQuantity>0){
                float notTrained = 0;
                float correct = 0;
                float  wrong = 0;

                for(String flashcardId: flashcardIds){
                    Flashcard flashcard = flashcardService.findByFlashcardId(flashcardId);
                    switch (flashcard.getStatus()) {
                        case NOTTRAINED -> notTrained+=1;
                        case CORRECT -> correct+=1;
                        case WRONG -> wrong+=1;
                        default -> {
                        }
                    }
                }
                Map<String,Float> statistic = new HashMap<>();
                statistic.put("NotTrained",(notTrained/flashcardQuantity)*100);
                statistic.put("Correct",(correct/flashcardQuantity)*100);
                statistic.put("Wrong",(wrong/flashcardQuantity)*100);
                flashcardSetGetDTO.setStatistic(statistic);
            }else{
                Map<String,Float> statistic = new HashMap<>();
                statistic.put("NotTrained",0F);
                statistic.put("Correct",0F);
                statistic.put("Wrong",0F);
                flashcardSetGetDTO.setStatistic(statistic);
            }
            flashcardSetsGetDTOs.add(flashcardSetGetDTO);
        }
        return flashcardSetsGetDTOs;
        
    }

    //get all flashcard inside of a set
    @GetMapping("/flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<FlashcardGetDTO> getAllFlashcardInSet(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId){
        userService.findByUserToken(userToken);
        FlashcardSet flashcardSet = flashcardService.findByFlashcardSetId(flashcardSetId);

        ArrayList<FlashcardGetDTO> flashcardGetDTOs = new ArrayList<>();

        for(String flashcardId : flashcardSet.getFlashcardsIds()){
            Flashcard flashcard = flashcardService.findByFlashcardId(flashcardId);
            flashcardGetDTOs.add(DTOMapper.INSTANCE.convertFlashcardEntityToFlashcardGetDTO(flashcard));
            
        }
        return flashcardGetDTOs;
    }

    //update flashcard with just new front (back is automatically translated) or updated front and back manually 
    @PutMapping("flashcards/{flashcardSetId}/{flashcardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updatedContentOfFlashcard(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId,@PathVariable String flashcardId, @RequestBody IncomingNewFlashcard incomingNewFlashcard){
        flashcardService.updateFlashcard(userToken,flashcardSetId,flashcardId,incomingNewFlashcard);
    }
    @PutMapping("flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updatedNameOfFlashcard(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId, @RequestBody IncomingNewFlashcardSet incomingNewFlashcardSet){
        flashcardService.updateFlashcardSetName(userToken,flashcardSetId,incomingNewFlashcardSet);
    }
    
    //delete one flashcard
    @DeleteMapping("flashcards/{flashcardSetId}/{flashcardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteFlashcard(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId, @PathVariable String flashcardId){
        flashcardService.deleteFlashcard(userToken,flashcardSetId,flashcardId);
    }
    //delete whole set
    @DeleteMapping("flashcards/{flashcardSetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteFlashcardSet(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId){
        flashcardService.deleteFlashcardSet(userToken,flashcardSetId);
    }
    //update status of a flashcard
    @PutMapping("flashcards/{flashcardSetId}/{flashcardId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateFlashcardStatus(@RequestHeader("Authorization") String userToken,@RequestHeader("Status") boolean status, @PathVariable String flashcardSetId,@PathVariable String flashcardId){
        flashcardService.updateFlashcardStatus(userToken,status,flashcardSetId,flashcardId);
    }
    //reset all flashcardStatus inside a flashcardSet
    @PutMapping("flashcards/{flashcardSetId}/status-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateFlashcardStatus(@RequestHeader("Authorization") String userToken, @PathVariable String flashcardSetId){
        flashcardService.resetFlashcardStatus(userToken,flashcardSetId);
    }
    
}
