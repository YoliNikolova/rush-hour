package com.prime.rushhour;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.AppointmentController;
import com.prime.rushhour.controllers.AuthenticationController;
import com.prime.rushhour.exception.ForbiddenException;
import com.prime.rushhour.exception.ThisEmailAlreadyExistsException;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.models.*;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.AppointmentService;
import com.prime.rushhour.services.UserService;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
@WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @Test
    public void authenticateWithTokenSuccess() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("yoli@abv.bg", "yoli9898");
        Authentication authentication = new UsernamePasswordAuthenticationToken(MyUserDetails.class, "");

        String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b2xpQGFidi5iZyIsImlhdCI6MTYxOTc3NDYxMCwiZXhwIjoxNjE5Nzc2NDEwfQ.3UgMjA_iTu6rcog9WBOrGk1jrN6CaZhguQXGZdiufzEVm7peRt_H-3XbRvB5QqtbCYOpoBAd3m5rBEfuZh9Yzg";
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwt);

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        Mockito.when(jwtUtil.createToken(authentication)).thenReturn(jwt);

        String url = "/authenticate";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertThat(response.getContentAsString(), equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(authenticationResponse)));
    }

    @Test
    public void authenticateWithTokenThrowException() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("yoli@abv.bg", "yoli9898");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(ForbiddenException.class);

        String url = "/authenticate";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898");
        UserResponseDTO user = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");

        Mockito.when(userService.registerUser(any(RegisterRequest.class))).thenReturn(user);

        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertThat(response.getContentAsString(), equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void registerThrowException() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898");

        Mockito.when(userService.registerUser(any(RegisterRequest.class))).thenThrow(ThisEmailAlreadyExistsException.class);

        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isConflict()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertTrue(result.getResolvedException() instanceof ThisEmailAlreadyExistsException);
    }

    @Test
    public void firstNameMustNotBeEmptyWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("", "Nikolova", "yoli@abv.bg", "yoli9898");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void lastNameMustNotBeEmptyWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "", "yoli@abv.bg", "yoli9898");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void emailMustBeInEmailFormatWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yeooeee", "yoli9898");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void emailMustNotBeEmptyWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "", "yoli9898");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void passwordMustNotBeEmptyWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void passwordMustBeWithMin8SymbolsWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void passwordMustBeWithMax20SymbolsWhenRegistration() throws Exception {
        RegisterRequest reg = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli98989898988888888888");
        String url = "/register";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(userService,Mockito.times(0)).registerUser(reg);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}
