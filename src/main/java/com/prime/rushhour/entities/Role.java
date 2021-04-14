package com.prime.rushhour.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    public Role() {
    }

    public Role(RoleType name) {
        this.name = name;
    }

    public String getName() {
        return this.name.name();
    }

    public void setName(String name) {
        this.name = RoleType.valueOf(name);
    }

    public List<User> getUsers() {
        return users;
    }
}
