package com.prime.rushhour.controllers;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping()
public class RoleController {
    private RoleService roleService;

    @RequestMapping("/home")
    public String homePage() {
        roleService.startFunc();
        return "Hello";
    }

    @RequestMapping("/roles")
    public List<String> getAllRoles() {
        return roleService.getAll().stream().map(Role::getName).collect(Collectors.toList());
    }

    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewRole(@RequestBody Role role) {
        roleService.add(role);
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
