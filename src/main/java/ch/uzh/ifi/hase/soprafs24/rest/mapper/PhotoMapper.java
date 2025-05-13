package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.Base64;

public class PhotoMapper {
    public String convertBytesToStringFormat(byte[] photo) {
        if (photo == null){
            return null;
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(photo);
      }
}
