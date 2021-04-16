package com.prime.rushhour;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class Bootstrapper implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findAll().size() == 0) {
            Role roleAdmin = new Role("ROLE_ADMIN");
            List<Role> listForAdmin = Collections.singletonList(roleAdmin);
            User admin = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9898", listForAdmin);
            userRepository.save(admin);
        }
    }
}
