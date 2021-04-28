package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.UserController;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.services.UserService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@WithMockUser()
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void getAllUsers() throws Exception {
       // Pageable page = PageRequest.of(0,2);
        List<UserResponseDTO> listUsers = new ArrayList<>();
        listUsers.add(new UserResponseDTO("Yoli","Nikolova","yoli@abv.bg"));
        listUsers.add(new UserResponseDTO("Vanesa","Angelova","nesi@abv.bg"));

        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(userService.getAll(pageable)).thenReturn(listUsers);

        String url = "/users";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(listUsers);

        assertThat(actualResponse,equalToIgnoringWhiteSpace(expectedResponse));
    }
}
