package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserEntities.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenValidInput_loginSuccess_returnsToken() throws Exception {
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("username");
        testUser.setId(1L);
        testUser.setToken("token");

        UserPostDTO userPostDTO  = new UserPostDTO();
        userPostDTO.setUsername(testUser.getUsername());
        userPostDTO.setPassword(testUser.getPassword());
        userPostDTO.setId(testUser.getId());

        given(userService.verifyLogin(any(User.class))).willReturn(testUser);

        MockHttpServletRequestBuilder request = post("/login").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(testUser.getToken())));

    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("firstname@lastname");

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].photo", nullValue()));
    }

    @Test
    public void givenUserId_whenGetAllFriends_theReturnJsonArray() throws Exception {
        User testUser = new User();
        testUser.setId(1L);

        User testFriend = new User();
        testFriend.setId(2L);

        testUser.setFriend(testFriend.getId());

        given(userService.findByUserId(testUser.getId())).willReturn(testUser);
        given(userService.findByUserId(testFriend.getId())).willReturn(testFriend);

        MockHttpServletRequestBuilder getRequest = get("/users/"+testUser.getId()+"/friends").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testFriend.getId().intValue())));
    }
    @Test
    public void getFriendRequestListWithUserId() throws Exception {
        User testReceiver = new User();
        testReceiver.setId(1L);

        User testSender = new User();
        testSender.setId(2L);

        testReceiver.setReceivedFriendRequest(testSender.getId());
        testSender.setSentFriendRequest(testReceiver.getId());

        given(userService.findByUserId(testReceiver.getId())).willReturn(testReceiver);
        given(userService.findByUserId(testSender.getId())).willReturn(testSender);

        MockHttpServletRequestBuilder getRequest = get("/users/" + testReceiver.getId() + "/friend-request").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testSender.getId().intValue())));
    }
        private String asJsonString ( final Object object){
            try {
                return new ObjectMapper().writeValueAsString(object);
            }
            catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("The request body could not be created.%s", e.toString()));
            }
        }
    }
