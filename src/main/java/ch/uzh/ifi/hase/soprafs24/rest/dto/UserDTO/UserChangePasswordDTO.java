package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

public class UserChangePasswordDTO {
    String oldPassword;
    String newPassword;

    public void setOldPassword(String oldPassword){
        this.oldPassword = oldPassword;
    }
    public String getOldPassword(){
        return oldPassword;
    }
    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
    }
    public String getNewPassword(){
        return newPassword;
    }
}
