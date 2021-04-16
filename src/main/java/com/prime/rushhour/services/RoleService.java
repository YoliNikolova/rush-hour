package com.prime.rushhour.services;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public void add(Role role) {
        roleRepository.save(role);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
