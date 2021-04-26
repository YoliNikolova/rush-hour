package com.prime.rushhour.services;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.ThisEmailAlreadyExistsException;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.models.RegisterRequest;
import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ModelMapper modelMapper;

    public UserResponseDTO getById(int id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> getAll(int pageNo,int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<User> pagedResult = userRepository.findAll(paging);
        return pagedResult.stream()
                .map(u -> modelMapper.map(u, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void add(UserRequestDTO newUser) {
        User user = modelMapper.map(newUser, User.class);
        List<Role> list = new ArrayList<>();
        userRepository.save(setRolesForUser(user, list));
    }

    public UserResponseDTO updateById(UserRequestDTO newUser, int id) {
        User user = modelMapper.map(newUser, User.class);
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException();
        }
        List<Role> list = new ArrayList<>();
        user.setId(id);
        userRepository.save(setRolesForUser(user, list));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    private User setRolesForUser(User user, List<Role> list) {
        for (Role r : user.getRoles()) {
            Optional<Role> currentRole = roleRepository.findByName(r.getName());
            if (currentRole.isEmpty()) {
                list.add(r);
            } else {
                list.add(currentRole.get());
            }
        }
        user.setRoles(list);
        return user;
    }

    public UserResponseDTO registerUser(RegisterRequest request) {
        User user = modelMapper.map(request, User.class);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ThisEmailAlreadyExistsException();
        }
        Optional<Role> roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles;
        if (roleUser.isEmpty()) {
            userRoles = Collections.singletonList(new Role("ROLE_USER"));
        } else {
            userRoles = Collections.singletonList(roleUser.get());
        }
        user.setRoles(userRoles);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public void delete(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
