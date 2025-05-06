package ch.uzh.ifi.hase.soprafs24.constant;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;

@Embeddable
public class ReadByUsers {

    private final ArrayList<Long> readByUsers = new ArrayList<>();

    @ElementCollection
    public ArrayList<Long> getReadByUsers() {return readByUsers;}
    public void addReadByUser(Long userId) {readByUsers.add(userId);}
}
