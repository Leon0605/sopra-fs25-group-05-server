package ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Chat;

@Repository("chatRepository")
public interface ChatRepository extends JpaRepository<Chat, String> {
    Chat findByChatId(String chatId);
}
