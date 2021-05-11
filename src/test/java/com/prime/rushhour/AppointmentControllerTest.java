package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.AppointmentController;
import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.exception.AppointmentExistsException;
import com.prime.rushhour.exception.AppointmentNotFoundException;
import com.prime.rushhour.exception.ForbiddenException;
import com.prime.rushhour.models.AppointmentRequestDTO;
import com.prime.rushhour.models.AppointmentResponseDTO;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.AppointmentService;
import org.junit.Before;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(AppointmentController.class)
@WithMockUser()
public class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    private MyUserDetails admin;
    private MyUserDetails user;

    @Before
    public void setUp() {
        admin = new MyUserDetails(new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("ROLE_ADMIN"))));
        user = new MyUserDetails(new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("ROLE_USER"))));
    }

    @Test
    public void getAllShouldReturnAllAppointments() throws Exception {
        List<AppointmentResponseDTO> list = new ArrayList<>();
        list.add(new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness"), new Activity("Yoga"))));
        list.add(new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 10, 17, 15), LocalDateTime.of(2021, 05, 10, 18, 45), Arrays.asList(new Activity("Fitness"))));

        when(appointmentService.getAll(any(Pageable.class), any(MyUserDetails.class))).thenReturn(list);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON).with(user(admin));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);
        String expectedResponse = objectMapper.writeValueAsString(list);

        assertThat(actualResponse, equalToIgnoringWhiteSpace(expectedResponse));
    }

    @Test
    public void getByIdShouldReturnAppointment() throws Exception {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentService.getById(anyInt(), any(MyUserDetails.class))).thenReturn(appResponse);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON).with(user(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);
        String expectedResponse = objectMapper.writeValueAsString(appResponse);

        assertThat(actualResponse, equalToIgnoringWhiteSpace(expectedResponse));
    }

    @Test
    public void getByIdShouldThrowForbiddenException() throws Exception {
        when(appointmentService.getById(anyInt(), any(MyUserDetails.class))).thenThrow(ForbiddenException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON).with(user(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isForbidden()).andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof ForbiddenException);
    }

    @Test
    public void getByIdShouldThrowNotFoundException() throws Exception {
        when(appointmentService.getById(anyInt(), any(MyUserDetails.class))).thenThrow(AppointmentNotFoundException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON).with(user(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof AppointmentNotFoundException);
    }

    @Test
    public void createNewAppointmentSuccess() throws Exception {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.add(any(AppointmentRequestDTO.class), anyInt())).thenReturn(appResponse);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertThat(response.getContentAsString(), equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(appResponse)));
    }

    @Test
    public void createNewAppointmentThrowActivityNotFound() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.add(any(AppointmentRequestDTO.class), anyInt())).thenThrow(ActivityNotFoundException.class);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResolvedException() instanceof ActivityNotFoundException);
    }

    @Test
    public void createNewAppointmentThrowExistingAppointment() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.add(any(AppointmentRequestDTO.class), anyInt())).thenThrow(AppointmentExistsException.class);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isConflict()).andReturn();

        assertTrue(result.getResolvedException() instanceof AppointmentExistsException);
    }

    @Test
    public void createAppointmentSuccessByUserThrowValidationException() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), null);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        verify(appointmentService, Mockito.times(0)).updateById(appRequest, 1, user);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void updateAppointmentSuccessByUser() throws Exception {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Yoga")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenReturn(appResponse);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getContentAsString(), equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(appResponse)));
    }

    @Test
    public void updateAppointmentThrowValidationException() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), null);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        verify(appointmentService, Mockito.times(0)).updateById(appRequest, 1, user);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void updateAppointmentSuccessByAdmin() throws Exception {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Yoga")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), 2, Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenReturn(appResponse);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(admin));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getContentAsString(), equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(appResponse)));
    }

    @Test
    public void updateAppointmentByUserThrowForbiddenException() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), 2, Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenThrow(ForbiddenException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResolvedException() instanceof ForbiddenException);
    }

    @Test
    public void updateAppointmentThrowAppointmentNotFoundException() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenThrow(AppointmentNotFoundException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException);
    }

    @Test
    public void updateAppointmentThrowActivityNotFoundException() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenThrow(ActivityNotFoundException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResolvedException() instanceof ActivityNotFoundException);
    }

    @Test
    public void updateAppointmentThrowExistingAppointment() throws Exception {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentService.updateById(any(AppointmentRequestDTO.class), anyInt(), any(MyUserDetails.class))).thenThrow(AppointmentExistsException.class);

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user));

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isConflict()).andReturn();

        assertTrue(result.getResolvedException() instanceof AppointmentExistsException);
    }

    @Test
    public void deleteByIdSuccess() throws Exception {
        doNothing().when(appointmentService).delete(anyInt(), any(MyUserDetails.class));

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON).with(user(user));
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        int status = result.getResponse().getStatus();

        verify(appointmentService, Mockito.times(1)).delete(1, user);
        assertEquals(status, HttpStatus.OK.value());
    }

    @Test
    public void deleteByIdThrowNotFoundException() throws Exception {
        doThrow(AppointmentNotFoundException.class).when(appointmentService).delete(anyInt(), any(MyUserDetails.class));

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON).with(user(user));
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

        verify(appointmentService, Mockito.times(1)).delete(1, user);
        assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException);
    }

    @Test
    public void deleteByIdThrowForbiddenException() throws Exception {
        doThrow(ForbiddenException.class).when(appointmentService).delete(anyInt(), any(MyUserDetails.class));

        String url = "/appointments/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON).with(user(user));
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isForbidden()).andReturn();

        verify(appointmentService, Mockito.times(1)).delete(1, user);
        assertTrue(result.getResolvedException() instanceof ForbiddenException);
    }
}
