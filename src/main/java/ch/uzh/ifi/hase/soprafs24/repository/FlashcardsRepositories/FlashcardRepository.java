package ch.uzh.ifi.hase.soprafs24.repository.FlashcardsRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.FlashcardEntities.Flashcard;

@Repository("flashcardRepository")
public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
    Flashcard findByFlashcardId(String flashcardId);
}
