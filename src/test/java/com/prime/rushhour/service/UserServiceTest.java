package com.prime.rushhour.service;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;


    @Test
    public void getUserByIdSuccess() {
        User user = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(responseUser);
        UserResponseDTO userById = userService.getById(anyInt());
        assertEquals(responseUser.getFirstName(), userById.getFirstName());
        assertEquals(responseUser.getEmail(), userById.getEmail());
        assertEquals(user.getEmail(), userById.getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdThrowException() {
        when(userRepository.findById(anyInt())).thenThrow(UserNotFoundException.class);
        UserResponseDTO userById = userService.getById(anyInt());
    }

    @Test
    public void getAllUsersSuccess() {
        User user = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));
        Page<User> users = Mockito.mock(Page.class);

        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");

       /* when(userRepository.findAll(any(Pageable.class))).thenReturn(users);
        when(users.stream()).thenAnswer(i->Stream.of(users));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(responseUser);
        InOrder inOrder = Mockito.inOrder(users);
        List<UserResponseDTO> userById = userService.getAll(any(Pageable.class));
        assertEquals(userById.size(), users.getSize());

        */
    }

    @Test
    public void addNewUserSuccess() {
        User user = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));
        User userWithSetRoles = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        UserRequestDTO userForRequest = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));


        when(userRepository.save(any(User.class))).thenReturn(userWithSetRoles);
        Mockito.when(modelMapper.map(org.mockito.ArgumentMatchers.any(),any())).thenReturn(user);
        Mockito.when(modelMapper.map(any(),any())).thenReturn(responseUser);
        UserResponseDTO addUser = userService.add(userRequestDTO);

        assertEquals(responseUser.getFirstName(), user.getFirstName());

    }

    @Test
    public void registerUserSuccess(){

    }
}
