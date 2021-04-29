package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.ActivityController;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.ActivityService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private ActivityService activityService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN","ROLE_USER"})
    public void getAllShouldReturnAllActivities() throws Exception {
        List<ActivityDTO> list = new ArrayList<>();
        list.add(new ActivityDTO("Fitness",60,5.00));
        list.add(new ActivityDTO("Manicure",60,20.00));

        Mockito.when(activityService.getAll(any(Pageable.class))).thenReturn(list);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(list);

        assertThat(actualResponse,equalToCompressingWhiteSpace(expectedResponse));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN","ROLE_USER"})
    public void getActivityByIdShouldReturnActivity() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",60,5.00);
        Mockito.when(activityService.getById(anyInt())).thenReturn(activity);
        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = result.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expected = objectMapper.writeValueAsString(activity);
        assertThat(actualResponse,equalToIgnoringWhiteSpace(expected));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN","ROLE_USER"})
    public void getActivityByIdShouldThrowException() throws Exception {
        Mockito.when(activityService.getById(anyInt())).thenThrow(ActivityNotFoundException.class);
        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().is4xxClientError()).andReturn();

        assertTrue(result.getResolvedException() instanceof ActivityNotFoundException);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createNewActivitySuccess() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",60,5.00);
        Mockito.when(activityService.add(any(ActivityDTO.class))).thenReturn(activity);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertThat(response.getContentAsString(),equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(activity)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void nameForActivityMustNotBeEmptyWhenCreating() throws Exception {
        ActivityDTO activity = new ActivityDTO("",60,5.00);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).add(activity);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void minutesForActivityMustGreaterThan5WhenCreating() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",1,5.00);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).add(activity);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void priceForActivityMustBePositiveWhenCreating() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",10,0);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).add(activity);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateActivitySuccess() throws Exception {
        ActivityDTO updateActivity = new ActivityDTO("Fitness",90,8.00);

        Mockito.when(activityService.updateById(any(ActivityDTO.class),anyInt())).thenReturn(updateActivity);

        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateActivity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getContentAsString(),equalToIgnoringWhiteSpace(objectMapper.writeValueAsString(updateActivity)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateActivityShouldThrowException() throws Exception {
        ActivityDTO updateActivity = new ActivityDTO("Fitness",90,8.00);

        Mockito.when(activityService.updateById(any(ActivityDTO.class),anyInt())).thenThrow(ActivityNotFoundException.class);

        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateActivity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().is4xxClientError()).andReturn();

        assertTrue(result.getResolvedException() instanceof ActivityNotFoundException);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void nameForActivityMustNotBeEmptyWhenUpdating() throws Exception {
        ActivityDTO activity = new ActivityDTO("",60,5.00);

        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).updateById(activity,1);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void minutesForActivityMustGreaterThan5WhenUpdating() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",1,5.00);

        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).updateById(activity,1);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void priceForActivityMustBePositiveWhenUpdating() throws Exception {
        ActivityDTO activity = new ActivityDTO("Fitness",10,0);

        String url = "/activities/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(activityService,Mockito.times(0)).updateById(activity,1);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteByIdSuccess() throws Exception {

        Mockito.doNothing().when(activityService).delete(anyInt());

        String url = "/activities/1";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        int status = result.getResponse().getStatus();
        Mockito.verify(activityService,Mockito.times(1)).delete(1);
        assertEquals(status,HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteByIdThrowException() throws Exception {
        Mockito.doThrow(ActivityNotFoundException.class).when(activityService).delete(anyInt());
        String url = "/activities/1";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();
        int status = result.getResponse().getStatus();
        
        Mockito.verify(activityService,Mockito.times(1)).delete(1);
        assertEquals(status,HttpStatus.NOT_FOUND.value());
    }
}
