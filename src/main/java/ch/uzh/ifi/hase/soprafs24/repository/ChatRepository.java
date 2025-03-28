package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.Chat;

@Repository("chatRepository")
public interface ChatRepository extends JpaRepository<Chat, String> {
    Chat findByChatId(String chatId);
}
