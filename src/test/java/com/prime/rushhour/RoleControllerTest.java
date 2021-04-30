package com.prime.rushhour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prime.rushhour.controllers.RoleController;
import com.prime.rushhour.entities.Role;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.security.MyUserDetailsService;
import com.prime.rushhour.services.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RoleController.class)
@WithMockUser(authorities = "ROLE_ADMIN")
public class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoleService roleService;

    @Test
    public void getAllRoles() throws Exception {
        List<Role> list = Arrays.asList(new Role("ROLE_ADMIN"),new Role("ROLE_USER"));

        Mockito.when(roleService.getAll()).thenReturn(list);
        String url = "/roles";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);

        List<String> namesRole = Arrays.asList("ROLE_ADMIN","ROLE_USER");

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);
        String expectedResponse = objectMapper.writeValueAsString(namesRole);

        assertThat(actualResponse,equalToIgnoringWhiteSpace(expectedResponse));
    }

    @Test
    public void createRoleSuccess() throws Exception {
        Role role = new Role("ROLE_USER");
        Mockito.doNothing().when(roleService).add(role);

        String url = "/roles";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(role))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }
}
