package ch.uzh.ifi.hase.soprafs24.repository.ChatsRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChatsEntities.Message;

@Repository("messageRepository")
public interface MessageRepository extends JpaRepository<Message, String> {
    Message findByMessageId(String messageId);
}
