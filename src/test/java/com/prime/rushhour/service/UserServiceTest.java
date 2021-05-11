package com.prime.rushhour.service;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.ThisEmailAlreadyExistsException;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.models.RegisterRequest;
import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private User user;

    @Before
    public void setUp() {
        user = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("ROLE_USER")));
    }

    @Test
    public void getUserByIdSuccess() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), any())).thenReturn(responseUser);
        UserResponseDTO userById = userService.getById(anyInt());
        assertEquals(responseUser.getFirstName(), userById.getFirstName());
        assertEquals(responseUser.getEmail(), userById.getEmail());
        assertEquals(user.getEmail(), userById.getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdThrowException() {
        when(userRepository.findById(anyInt())).thenThrow(UserNotFoundException.class);
        userService.getById(anyInt());
    }

    @Test
    public void getAllUsersSuccess() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<User> users = Mockito.mock(Page.class);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(users);
        List<UserResponseDTO> listOfUsers = userService.getAll(pageable);
        assertEquals(listOfUsers.size(), users.getSize());
    }

    @Test
    public void addNewUserSuccessWithExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        UserRequestDTO requestUser = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));

        when(modelMapper.map(requestUser, User.class)).thenReturn(user);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO addUser = userService.add(requestUser);

        assertSame(addUser.getEmail(), requestUser.getEmail());
    }

    @Test
    public void addNewUserSuccessWithNotExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        UserRequestDTO requestUser = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));

        when(modelMapper.map(requestUser, User.class)).thenReturn(user);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO addUser = userService.add(requestUser);

        assertSame(addUser.getEmail(), requestUser.getEmail());
    }

    @Test
    public void updateUserWithExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        UserRequestDTO requestUser = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));

        when(modelMapper.map(requestUser, User.class)).thenReturn(user);
        when(userRepository.existsById(1)).thenReturn(true);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role()));
        user.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO response = userService.updateById(requestUser, 1);

        assertSame(response.getEmail(), requestUser.getEmail());
    }

    @Test
    public void updateUserWithNotExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        UserRequestDTO requestUser = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));

        when(modelMapper.map(requestUser, User.class)).thenReturn(user);
        when(userRepository.existsById(1)).thenReturn(true);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        user.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO response = userService.updateById(requestUser, 1);

        assertSame(response.getEmail(), requestUser.getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserThrowException() {
        UserRequestDTO requestUser = new UserRequestDTO("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("User")));

        when(modelMapper.map(requestUser, User.class)).thenReturn(user);
        userService.updateById(requestUser, 1);

        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void registerUserWithExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        RegisterRequest request = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898");
        Role role = new Role("ROLE_USER");

        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        user.setRoles(Collections.singletonList(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO response = userService.registerUser(request);

        assertSame(response.getEmail(), request.getEmail());
    }

    @Test
    public void registerUserWithNotExistingRoles() {
        UserResponseDTO responseUser = new UserResponseDTO("Yoli", "Nikolova", "yoli@abv.bg");
        RegisterRequest request = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898");

        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        user.setRoles(Collections.singletonList(new Role("USER_ROLE")));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(responseUser);
        UserResponseDTO response = userService.registerUser(request);

        assertSame(response.getEmail(), request.getEmail());
    }

    @Test(expected = ThisEmailAlreadyExistsException.class)
    public void registerUserThrowException() {
        RegisterRequest request = new RegisterRequest("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898");

        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        userService.registerUser(request);

        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void deleteUserByIdSuccess() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserByIdThrowException() {
        when(userRepository.existsById(1)).thenReturn(false);
        userService.delete(1);
        verify(userRepository, times(0)).deleteById(1);
    }
}
