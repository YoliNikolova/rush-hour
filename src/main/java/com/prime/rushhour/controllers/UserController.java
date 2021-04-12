package com.prime.rushhour.controllers;

import com.prime.rushhour.models.UserRequestDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int id) {
        UserResponseDTO user = userService.getById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public List<UserResponseDTO> getAllUsers() {
        List<UserResponseDTO> allUsers = userService.getAll();
        return allUsers;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewUser(@RequestBody UserRequestDTO newUser) {
        userService.add(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserRequestDTO user, @PathVariable int id) {
        UserResponseDTO updateUser = userService.updateById(user, id);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        userService.delete(id);
    }
}
