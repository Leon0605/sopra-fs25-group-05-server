package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.repository.UsersRepositories.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup(){
        userRepository.deleteAll();
    }

    @Test
    public void createUserValidInputSuccess() throws Exception{
        UserPostDTO validInput = new UserPostDTO();
        validInput.setUsername("validUsername");
        validInput.setPassword("validPassword");

        MockHttpServletRequestBuilder postRequest = post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(validInput));
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void createUserDuplicateUsernameThrowsException() throws Exception{
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("token");
        userRepository.save(testUser);

        UserPostDTO duplicateUser = new UserPostDTO();
        duplicateUser.setUsername("Username");
        duplicateUser.setPassword("Password");

        MockHttpServletRequestBuilder postRequest = post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(duplicateUser));
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());

    }

    @Test
    public void getAllUsersSuccess() throws Exception{
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("token");

        User testUser2 = new User();
        testUser2.setUsername("Username2");
        testUser2.setPassword("Password2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setToken("token2");

        userRepository.save(testUser);
        userRepository.save(testUser2);

        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].username").value(testUser.getUsername()))
                .andExpect(jsonPath("$[1].username").value(testUser2.getUsername()));
    }

    @Test
    public void getOneUser_SomeoneElseOpenExists_Success() throws Exception{
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("token");
        testUser.setPrivacy("open");
        userRepository.save(testUser);

        MockHttpServletRequestBuilder getRequest = get("/users/" + testUser.getId()).contentType(MediaType.APPLICATION_JSON).header("Token", "someDifferentToken");
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    public void getOneUser_UserDoesNotExist_Failure() throws Exception{
        mockMvc.perform(get("/users/99999").contentType(MediaType.APPLICATION_JSON).header("Token", "someToken")).andExpect(status().isNotFound());
    }
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}