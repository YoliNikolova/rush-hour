package com.prime.rushhour.services;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public void add(Role role){
        roleRepository.save(role);
    }

    public List<Role> getAll(){
        return roleRepository.findAll();
    }

    public void startFunc() {
        if (userRepository.findAll().size() == 0) {
            Role role = new Role("ROLE_ADMIN");
            Role role2 = new Role("ROLE_USER");
            roleRepository.save(role);
            roleRepository.save(role2);
            List<Role> listForAdmin = new ArrayList<>();
            listForAdmin.add(role);
            User admin = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898", listForAdmin);
            userRepository.save(admin);
        }
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
