package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.ActivityController;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.services.ActivityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ActivityController.class)
@WithMockUser()
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActivityService activityService;

    @Test
    public void getAllActivities() throws Exception {
        List<ActivityDTO> list = new ArrayList<>();
        list.add(new ActivityDTO("Fitness",60,5.00));
        list.add(new ActivityDTO("Manicure",60,20.00));

        Mockito.when(activityService.getAll(Mockito.mock(Pageable.class))).thenReturn(list);

        String url = "/activities";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expectedResponse = objectMapper.writeValueAsString(list);

        assertThat(actualResponse,equalToIgnoringWhiteSpace(expectedResponse));
    }
    
    @Test
    public void getActivityById() throws Exception {
        ActivityDTO activity = new ActivityDTO("Yoga",90,6.00);
        Mockito.when(activityService.getById(Mockito.anyInt())).thenReturn(activity);
        String url = "users/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String actualResponse = result.getResponse().getContentAsString();
        System.out.println(actualResponse);

        String expected = objectMapper.writeValueAsString(activity);
        assertThat(actualResponse,equalToIgnoringWhiteSpace(expected));
    }
}
