package com.prime.rushhour.models;

import com.prime.rushhour.entities.Role;
import java.util.List;

public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Role> roles;


    public UserRequestDTO(){

    }

    public UserRequestDTO(String firstName,String lastName,String email,String password,List<Role> roles){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;
        this.roles=roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
