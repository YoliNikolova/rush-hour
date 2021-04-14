package com.prime.rushhour.services;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.models.RegisterRequest;
import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements BaseService<UserResponseDTO, UserRequestDTO> {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserResponseDTO getById(int id) {
        User user = userRepository.getOne(id);
        if (!userRepository.existsById(id)) {
            return null;
        }
        UserResponseDTO responseDTO = modelMapper.map(user, UserResponseDTO.class);
        return responseDTO;
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void add(UserRequestDTO newUser) {
        User user = modelMapper.map(newUser, User.class);
        List<Role> list = new ArrayList<>();
        checkRoles(user, list);
    }

    @Override
    public UserResponseDTO updateById(UserRequestDTO newUser, int id) {
        User user = modelMapper.map(newUser, User.class);
        List<Role> list = new ArrayList<>();
        user.setId(id);
        checkRoles(user, list);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    private void checkRoles(User user, List<Role> list) {
        for (Role r : user.getRoles()) {
            if (roleRepository.findByName(r.getName()).isEmpty()) {
                list.add(r);
            }else{
                list.add(roleRepository.findByName(r.getName()).get());
            }
        }
        user.setRoles(list);
        userRepository.save(user);
    }

    public UserResponseDTO registerUser(RegisterRequest request) {
        User user = modelMapper.map(request, User.class);
        List<Role> list = user.getRoles();
        list.add(roleRepository.findByName("ROLE_USER").get());
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public void delete(int id) {
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
}
