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
        for (Role r : user.getRoles()) {
           // if (roleRepository.findByName(r.getName()).isEmpty()) {
                roleRepository.save(r); // error with unique
           // }
            userRepository.save(user);
        }
    }

    @Override
    public UserResponseDTO updateById(UserRequestDTO newUser, int id) {
        User user = modelMapper.map(newUser, User.class);
        user.setId(id);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO registerUser(RegisterRequest request){
        User user = modelMapper.map(request,User.class);
        List<Role> list = user.getRoles();
        Role defaultRole = new Role("ROLE_USER");
        list.add(defaultRole);
        userRepository.save(user);
      //  roleRepository.save()
        return modelMapper.map(user,UserResponseDTO.class);
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
