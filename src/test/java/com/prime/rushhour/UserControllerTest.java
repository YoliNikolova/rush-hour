package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.UserController;
import com.prime.rushhour.entities.Role;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@WithMockUser(authorities = "ROLE_ADMIN")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void getAllShouldReturnAllUsers() throws Exception {
        List<UserResponseDTO> listUsers = new ArrayList<>();
        listUsers.add(new UserResponseDTO("Yoli","Nikolova","yoli@abv.bg"));
        listUsers.add(new UserResponseDTO("Vanesa","Angelova","nesi@abv.bg"));

        Mockito.when(userService.getAll(any(Pageable.class))).thenReturn(listUsers);

        String url = "/users";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(listUsers);

        assertThat(actualResponse,equalToIgnoringWhiteSpace(expectedResponse));
    }

    @Test
    public void getUserByIdShouldReturnUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO("Yoli","Nikolova","yoli@abv.bg");

        Mockito.when(userService.getById(Mockito.anyInt())).thenReturn(user);

        String url = "/users/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(user);

        assertThat(actualResponse,equalToIgnoringWhiteSpace(expectedResponse));
    }

    @Test
    public void getUserByIdShouldThrowException() throws Exception {
        Mockito.when(userService.getById(anyInt())).thenThrow(UserNotFoundException.class);
        String url = "/users/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
    }

    @Test
    public void createNewUserSuccess() throws Exception {
        UserResponseDTO user = new UserResponseDTO("Yoli","Nikolova","yoli@abv.bg");
        UserRequestDTO userForRequest = new UserRequestDTO("Yoli","Nikolova","yoli@abv.bg","yoli9818", Arrays.asList(new Role("User")));
        Mockito.when(userService.add(any(UserRequestDTO.class))).thenReturn(user);

        String url = "/users";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userForRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertThat(response.getContentAsString(),equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void updateUserSuccess() throws Exception {
        UserResponseDTO user = new UserResponseDTO("Yoli","Nikolova","yoli@abv.bg");
        UserRequestDTO userForRequest = new UserRequestDTO("Yoli","Nikolova","yoli@abv.bg","yoli9818", Arrays.asList(new Role("User")));

        Mockito.when(userService.updateById(any(UserRequestDTO.class),anyInt())).thenReturn(user);

        String url = "/users/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userForRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getContentAsString(),equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void updateUserShouldThrowException() throws Exception {
        UserRequestDTO userForRequest = new UserRequestDTO("Yoli","Nikolova","yoli@abv.bg","yoli9818", Arrays.asList(new Role("User")));

        Mockito.when(userService.updateById(any(UserRequestDTO.class),anyInt())).thenThrow(UserNotFoundException.class);

        String url = "/users/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userForRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
    }

    @Test
    public void deleteByIdSuccess() throws Exception {

        Mockito.doNothing().when(userService).delete(anyInt());

        String url = "/users/1";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        int status = result.getResponse().getStatus();
        Mockito.verify(userService,Mockito.times(1)).delete(1);
        assertEquals(status,HttpStatus.OK.value());
    }

    @Test
    public void deleteByIdThrowException() throws Exception {
        Mockito.doThrow(UserNotFoundException.class).when(userService).delete(anyInt());
        String url = "/users/1";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        Mockito.verify(userService,Mockito.times(1)).delete(1);
        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
    }
}
