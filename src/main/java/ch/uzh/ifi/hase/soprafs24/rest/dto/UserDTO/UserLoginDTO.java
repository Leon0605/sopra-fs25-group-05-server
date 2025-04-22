package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

public class UserLoginDTO extends UserGetDTO{
    private String token;

    public void setToken(String token){
        this.token=token;
    }

    public String getToken(){
        return token;
    }
}