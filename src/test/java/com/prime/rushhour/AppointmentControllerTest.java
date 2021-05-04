package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.AppointmentController;
import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.models.AppointmentResponseDTO;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.AppointmentService;
import com.prime.rushhour.services.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AppointmentController.class)
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


    @Test
    @WithMockUser
    public void getAllShouldReturnAllAppointments() throws Exception {

        // MyUserDetails user = new MyUserDetails(new User("Yoli","Nikolova","yoli@abv.bg","yoli9818", Arrays.asList(new Role("User"))));
        List<AppointmentResponseDTO> list = new ArrayList<>();
        list.add(new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness"), new Activity("Yoga"))));
        list.add(new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 10, 17, 15), LocalDateTime.of(2021, 05, 10, 18, 45), Arrays.asList(new Activity("Fitness"))));

        Mockito.when(appointmentService.getAll(any(Pageable.class), any(MyUserDetails.class))).thenReturn(list);

        String url = "/appointments";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(list);

        assertThat(actualResponse, equalToIgnoringWhiteSpace(expectedResponse));
    }
}
