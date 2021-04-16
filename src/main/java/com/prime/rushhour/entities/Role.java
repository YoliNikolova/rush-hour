package com.prime.rushhour.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }
}
