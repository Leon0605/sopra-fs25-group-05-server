package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

public class UserPostDTO {

  private String password;
  private String username;
  private Long id;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public void setPassword(String password){
    this.password=password;
  }
  public String getPassword(){
    return password;
  }
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
