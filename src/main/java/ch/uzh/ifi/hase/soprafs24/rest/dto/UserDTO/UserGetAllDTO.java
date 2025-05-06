package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

public class UserGetAllDTO {

    private Long id;
    private String username;
    private String photo;

    public void setId(Long id){this.id=id;}
    public void setUsername(String username){this.username=username;}
    public void setPhoto(String photo){this.photo=photo;}

    public Long getId(){return id;}
    public String getUsername(){return username;}
    public String getPhoto(){return photo;}
}
