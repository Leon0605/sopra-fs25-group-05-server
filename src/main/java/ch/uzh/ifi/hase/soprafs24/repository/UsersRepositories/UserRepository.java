package ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  //User findById(long id);
  User findByToken(String token);
}
