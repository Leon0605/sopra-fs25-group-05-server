package ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.FlashcardSet;

@Repository("flashcardSetRepository")
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, String> {
    FlashcardSet findByFlashcardSetId(String flashcardSetId);
}
